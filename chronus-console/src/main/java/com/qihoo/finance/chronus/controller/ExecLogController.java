package com.qihoo.finance.chronus.controller;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.core.log.service.JobExecLogService;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.log.entity.JobExecLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 日志管理
 *
 * @author chenxiyu
 * @date 2019/9/17
 */
@RestController
@Slf4j
@RequestMapping("/api/log")
public class ExecLogController {


    @Resource
    private JobExecLogService jobExecLogService;


    @RequestMapping(value = "/getAll", method = RequestMethod.POST)
    public Response getAllLog(@RequestBody JobExecLogEntity jobExecLogEntity) throws Exception {
        Response response = new Response().success();
        PageResult<JobExecLogEntity> jobExecLogEntityList = jobExecLogService.selectListByPage(jobExecLogEntity);
        response.setData(jobExecLogEntityList);
        return response;
    }

}
