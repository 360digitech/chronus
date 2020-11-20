package com.qihoo.finance.chronus.master.controller;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.context.ServiceContextHelper;
import com.qihoo.finance.chronus.master.service.TaskAssignRefreshService;
import com.qihoo.finance.chronus.metadata.api.assign.bo.WorkerTaskStateBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired(required = false)
    private TaskAssignRefreshService taskAssignRefreshService;

    @RequestMapping(value = "/pull/{address}/{dataVersion}", method = RequestMethod.POST)
    public DeferredResult<ResponseEntity<WorkerTaskStateBO>> pullAssignResult(@PathVariable("address") String address, @PathVariable("dataVersion") String dataVersion) throws UnsupportedEncodingException {
        ServiceContextHelper.initContext();
        return taskAssignRefreshService.pullAssignResult(URLDecoder.decode(address, "UTF-8"), dataVersion);
    }


    @RequestMapping(value = "/push/{address}/{dataVersion}", method = RequestMethod.POST)
    public Response pushLoadState(@PathVariable("address") String address, @PathVariable("dataVersion") String dataVersion, @RequestBody WorkerTaskStateBO workerTaskStateBO) {
        ServiceContextHelper.initContext();
        Response response = new Response().success();
        try {
            taskAssignRefreshService.submitWorkerState(URLDecoder.decode(address, "UTF-8"), dataVersion, workerTaskStateBO);
        } catch (Exception e) {
            response.hinderFail(e.getMessage());
        }
        return response;
    }
}
