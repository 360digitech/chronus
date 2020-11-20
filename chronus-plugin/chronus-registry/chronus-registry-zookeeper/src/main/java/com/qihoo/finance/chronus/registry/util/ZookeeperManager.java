package com.qihoo.finance.chronus.registry.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


@Slf4j
public class ZookeeperManager {
    private CuratorFramework curator;
    private List<ACL> acl = new ArrayList<>();
    private Properties properties;

    public enum keys {
        zkConnectString, rootPath, userName, password, zkSessionTimeout, zkConnectionTimeout
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
        String userName = properties.getProperty(keys.userName.toString());
        String zkConnectString = properties.getProperty(keys.zkConnectString.toString());
        int zkSessionTimeout = Integer.parseInt(properties.getProperty(keys.zkSessionTimeout.toString()));
        int zkConnectionTimeout = Integer.parseInt(properties.getProperty(keys.zkConnectionTimeout.toString()));
        String authString = userName + ":" + properties.getProperty(keys.password.toString());
        acl.clear();
        acl.add(new ACL(ZooDefs.Perms.ALL, new Id("digest", DigestAuthenticationProvider.generateDigest(authString))));
        acl.add(new ACL(ZooDefs.Perms.READ, Ids.ANYONE_ID_UNSAFE));
        log.info("----------------------------开始创建ZK连接----------------------------");
        log.info("zkConnectString:{}", zkConnectString);
        log.info("zkSessionTimeout:{}", zkSessionTimeout);
        log.info("zkConnectionTimeout:{}", zkConnectionTimeout);
        log.info("userName:{}", userName);

        curator = CuratorFrameworkFactory.builder().connectString(zkConnectString)
                .sessionTimeoutMs(zkSessionTimeout)
                .connectionTimeoutMs(zkConnectionTimeout)
                .retryPolicy(new RetryNTimes(1, 1000))
                .authorization("digest", authString.getBytes())
                .aclProvider(new ACLProvider() {
                    @Override
                    public List<ACL> getDefaultAcl() {
                        return acl;
                    }

                    @Override
                    public List<ACL> getAclForPath(String path) {
                        return acl;
                    }
                })
                .build();
        curator.start();
        boolean connected = curator.blockUntilConnected(zkConnectionTimeout, TimeUnit.MILLISECONDS);
        if (!connected) {
            throw new IllegalStateException("zookeeper not connected zkConnectString:" + zkConnectString);
        }
        String rootPath = getRootPath();
        boolean flag = checkExists(rootPath);
        log.info("----------------------------创建ZK连接成功 RootPath:{} Exists:{}----------------------------", rootPath, flag);
    }

    public void close() {
        log.info("关闭zookeeper连接");
        if (curator == null) {
            return;
        }
        this.curator.close();
    }

    public String getRootPath() {
        return properties.getProperty(keys.rootPath.toString());
    }

    public void initial() throws Exception {
        //当zk状态正常后才能调用
        // 根路径初始化
        if (curator.checkExists().forPath(this.getRootPath()) == null) {
            createPath(curator, this.getRootPath(), CreateMode.PERSISTENT, acl);
            //设置版本信息
            curator.setData().forPath(this.getRootPath(), Version.getVersion().getBytes());
        } else {
            byte[] value = curator.getData().forPath(this.getRootPath());
            if (value == null) {
                curator.setData().forPath(this.getRootPath(), Version.getVersion().getBytes());
            } else {
                String dataVersion = new String(value);
                if (!Version.isCompatible(dataVersion)) {
                    throw new Exception("chronus程序版本 " + Version.getVersion() + " 不兼容Zookeeper中的数据版本 " + dataVersion);
                }
                log.info("当前的程序版本:" + Version.getVersion() + " 数据版本: " + dataVersion + " ZK 根路径初始化完成!");
            }
        }
    }

    private List<ACL> getAcl() {
        return acl;
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

    public CuratorOp createSetDataOp(String path, String data) throws Exception {
        CuratorOp setDataOp = this.curator.transactionOp().setData().forPath(path, data.getBytes());
        return setDataOp;
    }

    public List<CuratorTransactionResult> forOperations(List<CuratorOp> curatorOps) throws Exception {
        List<CuratorTransactionResult> results = this.curator.transaction().forOperations(curatorOps);
        return results;
    }
}
