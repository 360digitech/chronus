package com.qihoo.finance.chronus.controller;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.util.ControllerUtil;
import com.qihoo.finance.chronus.core.log.NodeLogService;
import com.qihoo.finance.chronus.metadata.api.log.entity.NodeLogEntity;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.registry.api.Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xiongpu on 2018/8/21.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/node")
public class NodeController {

    @Resource
    private NamingService namingService;

    @Resource
    private NodeLogService nodeLogService;
    /**
     * 机器下线
     *
     * @param node
     * @param bindingResult
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/offline", method = RequestMethod.PUT)
    public Response offline(@RequestBody @Valid Node node, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        try {
            if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
                return response;
            }
            namingService.offlineNode(node);
        } catch (Exception e) {
            log.error("机器下线异常 nodeEntity:{}", e);
            response.hinderFail("机器下线异常" + e.getMessage());
        }
        return response;
    }

    /**
     * 机器下线
     *
     * @param node
     * @param bindingResult
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/online", method = RequestMethod.PUT)
    public Response online(@RequestBody @Valid Node node, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        try {
            if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
                return response;
            }
            namingService.onlineNode(node);
        } catch (Exception e) {
            log.error("机器上线异常 nodeEntity:{}", e);
            response.hinderFail("机器上线异常" + e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/getAllNode", method = RequestMethod.POST)
    public Response getAllNode() throws Exception {
        Response response = new Response().success();
        List<Node> allNode = namingService.getAllNode();
        response.setData(allNode);
        return response;
    }


    @RequestMapping(value = "/log/{cluster}/{address}/{version}", method = RequestMethod.GET)
    public Response getAllNode(@PathVariable(name = "cluster") String cluster, @PathVariable(name = "address") String address, @PathVariable(name = "version") String version) throws Exception {
        Response response = new Response().success();
        List<NodeLogEntity> allEvent = nodeLogService.getLog(cluster, address);
        response.setData(allEvent);
        return response;
    }
}
