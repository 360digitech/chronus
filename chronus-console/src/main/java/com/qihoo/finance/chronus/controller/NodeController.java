package com.qihoo.finance.chronus.controller;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.util.ControllerUtil;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.registry.api.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public Response getAllNode(@RequestBody Node nodeEntity) throws Exception {
        Response response = new Response().success();
        if(nodeEntity.getCluster()== null || "".equals(nodeEntity.getCluster())){
            List<Node> allNode = new ArrayList<>();
            response.setData(allNode);
            return response;
        }
        List<Node> allNode = namingService.getAllNode(nodeEntity.getCluster());
        response.setData(allNode);
        return response;
    }

}
