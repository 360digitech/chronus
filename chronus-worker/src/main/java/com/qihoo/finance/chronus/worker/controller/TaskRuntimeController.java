package com.qihoo.finance.chronus.worker.controller;

import com.alibaba.fastjson.JSON;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import com.qihoo.finance.chronus.worker.service.WorkerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xiongpu on 2019/9/16.
 */
@Slf4j
@RestController
@RequestMapping("/api/worker")
public class TaskRuntimeController {

    @Autowired(required = false)
    private WorkerService workerService;

    @RequestMapping(value = "/getTaskRuntimeInfo/{taskName}/{taskItemSeqNo}", method = RequestMethod.GET)
    public TaskRuntimeEntity getTaskRuntimeInfo(@PathVariable("taskName") String taskName, @PathVariable("taskItemSeqNo") String taskItemSeqNo) {
        return workerService.getTaskRuntimeInfo(taskName, taskItemSeqNo);
    }

    @RequestMapping(value = "/getTaskRuntimeStateOfError")
    public String getTaskRuntimeStateOfError() {
        if (workerService.getTaskRuntimeStateOfError().size() > 0) {
            return JSON.toJSONString(workerService.getTaskRuntimeStateOfError());
        }
        return "ok";
    }
}
