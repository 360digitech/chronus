package com.qihoo.finance.chronus.master.controller;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.context.SpringContextHolder;
import com.qihoo.finance.chronus.master.service.TaskAssignService;
import com.qihoo.finance.chronus.metadata.api.assign.bo.ExecutorTaskStateBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by xiongpu on 2019/9/16.
 */
@Slf4j
@RestController
@RequestMapping("/api/master")
public class TaskAssignResultController {

    @RequestMapping(value = "/pull/{address}", method = RequestMethod.POST)
    public DeferredResult<ResponseEntity<ExecutorTaskStateBO>> pullAssignResult(@PathVariable("address") String address, @RequestBody ExecutorTaskStateBO executorTaskStateBO) throws UnsupportedEncodingException {
        return SpringContextHolder.getBean(TaskAssignService.class).pullAssignResult(URLDecoder.decode(address, "UTF-8"),executorTaskStateBO);
    }


    @RequestMapping(value = "/push/{address}", method = RequestMethod.POST)
    public Response pushLoadState(@PathVariable("address") String address, @RequestBody ExecutorTaskStateBO executorTaskStateBO) {
        Response response = new Response().success();
        try {
            SpringContextHolder.getBean(TaskAssignService.class).submitExecutorState(URLDecoder.decode(address, "UTF-8"),executorTaskStateBO);
        } catch (Exception e) {
            response.hinderFail(e.getMessage());
        }
        return response;
    }
}
