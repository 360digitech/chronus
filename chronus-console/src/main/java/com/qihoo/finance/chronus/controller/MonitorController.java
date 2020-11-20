package com.qihoo.finance.chronus.controller;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.core.cluster.ClusterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * @author xiongpu
 * @date 2020/6/17.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/monitor")
public class MonitorController {

    @Resource
    private ClusterService clusterService;

    @RequestMapping(value = "/dataGuard/start/{cluster}", method = RequestMethod.GET)
    public Response dataGuardStart(@PathVariable(name = "cluster") String cluster) throws Exception {
        log.info("准备开始集群容灾任务转移,任务使用指定集群调度!:{}", cluster);
        Response response = new Response().success();
        try {
            clusterService.dataGuardStart(cluster);
            log.info("已经开始集群容灾任务转移,任务使用指定集群调度!:{}", cluster);
        } catch (Exception e) {
            log.error("启动容灾异常! cluster:{}", cluster, e);
            response.fail("", e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/dataGuard/stop/{cluster}", method = RequestMethod.GET)
    public Response dataGuardStop(@PathVariable(name = "cluster") String cluster) throws Exception {
        Response response = new Response().success();
        log.info("准备停止集群容灾任务转移,任务恢复原指定集群调度!:{}", cluster);
        try {
            clusterService.dataGuardStop(cluster);
            log.info("已经停止集群容灾任务转移,任务恢复原指定集群调度!:{}", cluster);
        } catch (Exception e) {
            log.error("停止容灾异常! cluster:{}", cluster, e);
            response.fail("", e.getMessage());
        }
        return response;
    }
}
