package com.qihoo.finance.chronus.controller;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.util.ControllerUtil;
import com.qihoo.finance.chronus.common.util.SecureUtils;
import com.qihoo.finance.chronus.core.cluster.ClusterService;
import com.qihoo.finance.chronus.metadata.api.cluster.entity.ClusterEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;


/**
 * Created by xiongpu on 2018/8/21.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/cluster")
public class ClusterController {

    @Resource
    private ClusterService clusterService;

    /**
     * 新增环境集群配置
     *
     * @param clusterEntity
     * @param bindingResult
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Response insert(@RequestBody @Valid ClusterEntity clusterEntity, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        try {
            if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
                return response;
            }
            if (StringUtils.isBlank(clusterEntity.getCluster())) {
                clusterEntity.setCluster(ChronusConstants.DEF_CLUSTER);
            }
            String user = (String) SecureUtils.getPrincipal();
            clusterEntity.setCreatedBy(user);
            clusterEntity.setUpdatedBy(user);
            clusterService.insert(clusterEntity);
        } catch (Exception e) {
            log.error("新增环境配置异常! envEntity:{}", clusterEntity, e);
            response.hinderFail("新增环境配置异常" + e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/{cluster}", method = RequestMethod.DELETE)
    public Response delete(@PathVariable("cluster") String cluster) throws Exception {
        Response response = new Response().success();
        try {
            clusterService.delete(cluster);
        } catch (Exception e) {
            log.error("删除环境配置异常! cluster:{}", cluster, e);
            response.hinderFail("删除环境配置异常" + e.getMessage());
        }
        return response;
    }

    /**
     * 修改环境集群配置
     *
     * @param clusterEntity
     * @param bindingResult
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public Response update(@RequestBody @Valid ClusterEntity clusterEntity, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        try {
            if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
                return response;
            }
            // 如果想开启集群,但是已有集群启动容灾状态,则不能开启
            String user = (String) SecureUtils.getPrincipal();
            clusterEntity.setUpdatedBy(user);
            clusterEntity.setDateUpdated(new Date());
            clusterService.update(clusterEntity);
        } catch (Exception e) {
            log.error("更新环境配置异常! clusterEntity:{}", clusterEntity, e);
            response.hinderFail("更新环境配置异常" + e.getMessage());
        }
        return response;
    }

    /**
     * 获取所有环境列表
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getAllCluster", method = RequestMethod.GET)
    public Response getAllCluster() throws Exception {
        Response response = new Response().success();
        List<ClusterEntity> envEntityList = clusterService.selectListAll();
        response.setData(envEntityList);
        return response;
    }

}
