package com.qihoo.finance.chronus.controller;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.core.event.service.EventService;
import com.qihoo.finance.chronus.metadata.api.event.entity.EventEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by xiongpu on 2018/8/21.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/event")
public class EventController {

    @Resource
    private EventService eventService;

    @RequestMapping(value = "/getAllEvent/{cluster}/{address}/{version}", method = RequestMethod.GET)
    public Response getAllNode(@PathVariable(name = "cluster") String cluster, @PathVariable(name = "address") String address, @PathVariable(name = "version") String version) throws Exception {
        Response response = new Response().success();
        List<EventEntity> allEvent = eventService.getAllEvent(cluster, address, version);
        response.setData(allEvent);
        return response;
    }

}
