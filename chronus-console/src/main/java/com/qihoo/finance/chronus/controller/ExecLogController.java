package com.qihoo.finance.chronus.controller;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.spi.UserService;
import com.qihoo.finance.chronus.common.util.SecureUtils;
import com.qihoo.finance.chronus.core.log.JobExecLogService;
import com.qihoo.finance.chronus.core.system.SystemGroupService;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.log.entity.JobExecLogEntity;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Resource
    private SystemGroupService systemGroupService;

    @Resource
    private UserService userService;

    @RequestMapping(value = "/getAll", method = RequestMethod.POST)
    public Response getAllLog(@RequestBody JobExecLogEntity jobExecLogEntity) throws Exception {
        String userNo = (String) SecureUtils.getPrincipal();
        UserEntity user = userService.findByUserNo(userNo);
        List<String> sysCodes;
        String[] arr = user.getGroup().split(",");
        List groups = CollectionUtils.arrayToList(arr);
        if (groups.contains(ChronusConstants.ALL)) {
            sysCodes = new ArrayList<>(systemGroupService.loadAllSysCodes());
        } else {
            List<String> list = new ArrayList<>();
            list.addAll(groups);
            sysCodes = systemGroupService.loadSystemGroupByGroupName(list).stream().map(s -> s.getSysCode()).collect(Collectors.toList());
        }
        PageResult<JobExecLogEntity> jobExecLogEntityList = jobExecLogService.selectListByPage(jobExecLogEntity, sysCodes);
        Response response = new Response().success();
        response.setData(jobExecLogEntityList);
        return response;
    }

}
