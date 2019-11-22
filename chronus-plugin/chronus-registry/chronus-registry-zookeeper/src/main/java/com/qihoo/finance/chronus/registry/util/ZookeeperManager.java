package com.qihoo.finance.chronus.registry.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;


@Slf4j
public class ZookeeperManager {
    private CuratorFramework curator;
    private List<ACL> acl = new ArrayList<>();
    private Properties properties;
    private boolean isCheckParentPath = true;

    public enum keys {
        zkConnectString, rootPath, userName, password, zkSessionTimeout, zkConnectionTimeout, isCheckParentPath
    }

    public ZookeeperManager(Properties properties) throws Exception {
        this.properties = properties;
        this.connect();
    }

    /**
     * 重连zookeeper
     *
     * @throws Exception
     */
    public synchronized void reConnection() throws Exception {
        if (this.curator != null) {
            this.curator.close();
            this.curator = null;
            this.connect();
        }
    }

    private void connect() throws Exception {
        RetryPolicy retryPolicy = new RetryUntilElapsed(Integer.MAX_VALUE, 10);
        String userName = properties.getProperty(keys.userName.toString());
        String zkConnectString = properties.getProperty(keys.zkConnectString.toString());
        int zkSessionTimeout = Integer.parseInt(properties.getProperty(keys.zkSessionTimeout.toString()));
        int zkConnectionTimeout = Integer.parseInt(properties.getProperty(keys.zkConnectionTimeout.toString()));
        boolean isCheckParentPath = Boolean.parseBoolean(properties.getProperty(keys.isCheckParentPath.toString(), "true"));
        String authString = userName + ":" + properties.getProperty(keys.password.toString());
        acl.clear();
        acl.add(new ACL(ZooDefs.Perms.ALL, new Id("digest", DigestAuthenticationProvider.generateDigest(authString))));
        acl.add(new ACL(ZooDefs.Perms.READ, Ids.ANYONE_ID_UNSAFE));
        log.info("----------------------------开始创建ZK连接----------------------------");
        log.info("zkConnectString:{}", zkConnectString);
        log.info("zkSessionTimeout:{}", zkSessionTimeout);
        log.info("zkConnectionTimeout:{}", zkConnectionTimeout);
        log.info("isCheckParentPath:{}", isCheckParentPath);
        log.info("userName:{}", userName);

        curator = CuratorFrameworkFactory.builder().connectString(zkConnectString)
                .sessionTimeoutMs(zkSessionTimeout)
                .connectionTimeoutMs(zkConnectionTimeout)
                .retryPolicy(retryPolicy).authorization("digest", authString.getBytes())
                .aclProvider(new ACLProvider() {
                    @Override
                    public List<ACL> getDefaultAcl() {
                        return ZooDefs.Ids.CREATOR_ALL_ACL;
                    }

                    @Override
                    public List<ACL> getAclForPath(String path) {
                        return ZooDefs.Ids.CREATOR_ALL_ACL;
                    }
                }).build();
        curator.start();
        log.info("----------------------------创建ZK连接成功----------------------------");
        this.isCheckParentPath = isCheckParentPath;
    }

    public void close() throws InterruptedException {
        log.info("关闭zookeeper连接");
        if (curator == null) {
            return;
        }
        this.curator.close();
    }

    public String getRootPath() {
        return properties.getProperty(keys.rootPath.toString());
    }

    public boolean checkZookeeperState() throws Exception {
        return curator != null && curator.getState() == CuratorFrameworkState.STARTED && curator.getZookeeperClient().isConnected();
    }

    public void initial() throws Exception {
        //当zk状态正常后才能调用
        // 根路径初始化
        if (curator.checkExists().forPath(this.getRootPath()) == null) {
            createPath(curator, this.getRootPath(), CreateMode.PERSISTENT, acl);
            if (isCheckParentPath) {
                checkParent(curator, this.getRootPath());
            }
            //设置版本信息
            curator.setData().forPath(this.getRootPath(), Version.getVersion().getBytes());
        } else {
            //先校验父亲节点，本身是否已经是schedule的目录
            if (isCheckParentPath) {
                checkParent(curator, this.getRootPath());
            }
            byte[] value = curator.getData().forPath(this.getRootPath());
            if (value == null) {
                curator.setData().forPath(this.getRootPath(), Version.getVersion().getBytes());
            } else {
                String dataVersion = new String(value);
                if (Version.isCompatible(dataVersion) == false) {
                    throw new Exception("chronus程序版本 " + Version.getVersion() + " 不兼容Zookeeper中的数据版本 " + dataVersion);
                }
                log.info("当前的程序版本:" + Version.getVersion() + " 数据版本: " + dataVersion + " ZK 根路径初始化完成!");
            }
        }
    }

    public static void checkParent(CuratorFramework curator, String path) throws Exception {
        String[] list = path.split("/");
        String zkPath = "";
        for (int i = 0; i < list.length - 1; i++) {
            String str = list[i];
            if ("".equals(str) == false) {
                zkPath = zkPath + "/" + str;
                if (curator.checkExists().forPath(zkPath) != null) {
                    byte[] value = curator.getData().forPath(zkPath);
                    if (value != null) {
                        String tmpVersion = new String(value);
                        if (tmpVersion.indexOf("chronus-schedule-") >= 0) {
                            throw new Exception("\"" + zkPath + "\"  is already a schedule instance's root directory, its any subdirectory cannot as the root directory of others");
                        }
                    }
                }
            }
        }
    }

    private List<ACL> getAcl() {
        return acl;
    }

    public CuratorFramework getCurator() throws Exception {
        if (this.checkZookeeperState() == false) {
            reConnection();
        }
        return this.curator;
    }

    public boolean checkExists(String path) throws Exception {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return checkExistsGetStat(path) != null;
    }

    public Stat checkExistsGetStat(String path) throws Exception {
        Stat stat = this.curator.checkExists().forPath(path);
        return stat;
    }

    public boolean delete(String path) throws Exception {
        if (checkExists(path)) {
            this.curator.delete().deletingChildrenIfNeeded().forPath(path);
            return true;
        }
        return false;
    }

    public Stat setData(String path, byte[] data) throws Exception {
        if (checkExists(path)) {
            return this.curator.setData().withVersion(-1).forPath(path, data);
        }
        return null;
    }

    public byte[] getData(String path) throws Exception {
        return this.curator.getData().forPath(path);
    }

    public byte[] getData(String path, Stat stat) throws Exception {
        return this.curator.getData().storingStatIn(stat).forPath(path);
    }

    public static String createPath(CuratorFramework curator, String path, CreateMode createMode, List<ACL> acls) throws Exception {
        if (curator.checkExists().forPath(path) == null) {
            return curator.create().creatingParentsIfNeeded().withMode(createMode).withACL(acls).forPath(path, null);
        }
        return path;
    }

    public String createPath(String path, CreateMode createMode) throws Exception {
        return createPath(this.curator, path, createMode, getAcl());
    }

    public List<String> getChildren(String path) throws Exception {
        if (checkExists(path)) {
            return this.curator.getChildren().forPath(path);
        }
        return new ArrayList<>();
    }



    public void deleteTree(String path) throws Exception {
        if (!checkExists(path)) {
            return;
        }
        List<String> pathChildren = this.curator.getChildren().forPath(path);
        if (pathChildren == null || pathChildren.size() == 0) {
            this.curator.delete().guaranteed().deletingChildrenIfNeeded().withVersion(-1).forPath(path);
        } else {
            for (String cpath : pathChildren) {
                deleteTree(path + "/" + cpath);
            }
            this.curator.delete().guaranteed().deletingChildrenIfNeeded().withVersion(-1).forPath(path);
        }
    }

}
