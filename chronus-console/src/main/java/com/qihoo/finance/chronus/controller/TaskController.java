package com.qihoo.finance.chronus.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.spi.UserService;
import com.qihoo.finance.chronus.common.util.ControllerUtil;
import com.qihoo.finance.chronus.common.util.SecureUtils;
import com.qihoo.finance.chronus.common.util.ValidationResult;
import com.qihoo.finance.chronus.common.util.ValidationUtils;
import com.qihoo.finance.chronus.context.SpringContextHolder;
import com.qihoo.finance.chronus.core.log.JobExecLogService;
import com.qihoo.finance.chronus.core.system.SystemGroupService;
import com.qihoo.finance.chronus.core.task.TaskItemService;
import com.qihoo.finance.chronus.core.task.TaskService;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author chenxiyu
 * @date 2019/9/18.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/task")
public class TaskController {

    @Resource
    private TaskService taskService;

    @Resource
    private UserService userService;

    @Resource
    private SystemGroupService systemGroupService;

    @Resource
    private TaskItemService taskItemService;

    @Resource
    private JobExecLogService jobExecLogService;

    @Resource
    private RestTemplate restTemplate;


    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Response insert(@RequestBody @Valid TaskEntity taskEntity, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        try {
            if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
                return response;
            }
            if (validate(taskEntity, response).failed()) {
                return response;
            }
            TaskEntity dbTask = taskService.selectTaskInfoByTaskName(taskEntity.getTaskName());
            if (dbTask != null) {
                return response.hinderFail("无法创建同名的任务![" + dbTask.getDealSysCode() + ":" + dbTask.getTaskName() + "]");
            }

            String user = (String) SecureUtils.getPrincipal();
            taskEntity.setCreatedBy(user);
            taskEntity.setUpdatedBy(user);
            taskService.insert(taskEntity);
        } catch (Exception e) {
            log.error("新增任务异常! tagEntity:{}", taskEntity, e);
            response.hinderFail("新增任务异常" + e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/{cluster}/{dealSysCode}/{taskName}", method = RequestMethod.DELETE)
    public Response delete(@PathVariable("cluster") String cluster,
                           @PathVariable("dealSysCode") String dealSysCode,
                           @PathVariable("taskName") String taskName) throws Exception {
        Response response = new Response().success();
        try {
            String userNo = (String) SecureUtils.getPrincipal();
            UserEntity user = userService.findByUserNo(userNo);
            List<String> dealSysCodes = getDealSysCodes();
            if (!dealSysCodes.contains(dealSysCode)) {
                log.error("禁止操作没有权限的系统! userNo:{} ,dealSysCode:{},taskName:{}", userNo, dealSysCode, taskName);
                return response.hinderFail("权限错误!");
            }

            log.warn("用户:{}删除任务:{},{},{}", user, cluster, dealSysCode, taskName);
            taskService.delete(taskName);
            //删除任务时 删除任务执行日志(此处可以作为一个可选项)
            jobExecLogService.delete(taskName);
        } catch (Exception e) {
            log.error("删除任务异常! taskName:{}", taskName, e);
            response.hinderFail("删除任务异常" + e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public Response put(@RequestBody @Valid TaskEntity taskEntity, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        try {
            if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
                return response;
            }
            if (taskEntity == null || taskEntity.getId() == null) {
                return response.hinderFail("taskEntity.id为空,无法更新!");
            }
            if (validate(taskEntity, response).failed()) {
                return response;
            }

            String user = (String) SecureUtils.getPrincipal();
            taskEntity.setUpdatedBy(user);
            taskService.update(taskEntity);
        } catch (Exception e) {
            log.error("更新任务异常! tagEntity:{}", taskEntity, e);
            response.hinderFail("更新任务异常" + e.getMessage());
        }
        return response;
    }

    private Response validate(TaskEntity taskEntity, Response response) {
        ValidationResult validationResult = ValidationUtils.validateEntity(taskEntity);
        if (validationResult.isHasErrors()) {
            return response.hinderFail(validationResult.getErrorMessage());
        }
        if (StringUtils.isNotBlank(taskEntity.getPermitRunStartTime())) {
            try {
                CronExpression.validateExpression(taskEntity.getPermitRunStartTime());
            } catch (Exception e) {
                return response.hinderFail("开始执行时间配置有误,请检查:" + e.getMessage());
            }
        }
        return response;
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public Response detail(TaskEntity taskEntity) throws Exception {
        Response response = new Response().success();
        taskService.selectById(taskEntity.getId());
        return response;
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.POST)
    public Response getAllTask(@RequestBody TaskEntity taskEntity) throws Exception {
        Response response = new Response().success();
        List<String> dealSysCodes = getDealSysCodes();
        PageResult<TaskEntity> pageResult = taskService.selectListByPage(taskEntity, dealSysCodes);
        response.setData(pageResult);
        return response;
    }

    private List<String> getDealSysCodes() {
        String userNo = (String) SecureUtils.getPrincipal();
        UserEntity user = userService.findByUserNo(userNo);
        List<String> dealSysCodes;
        String[] arr = user.getGroup().split(",");
        List groups = CollectionUtils.arrayToList(arr);
        if (groups.contains(ChronusConstants.ALL)) {
            dealSysCodes = new ArrayList<>(systemGroupService.loadAllSysCodes());
        } else {
            List<String> list = new ArrayList<>();
            list.addAll(groups);
            dealSysCodes = systemGroupService.loadSystemGroupByGroupName(list).stream().map(s -> s.getSysCode()).collect(Collectors.toList());
        }
        return dealSysCodes;
    }

    @RequestMapping(value = "/taskRuntime", method = RequestMethod.POST)
    public Response taskRuntime(@RequestBody TaskEntity taskEntity) throws Exception {
        Response response = new Response().success();
        List<TaskItemEntity> taskItemEntityList = taskItemService.getTaskItemList(taskEntity.getTaskName());
        JSONArray jsonArray = new JSONArray();
        for (TaskItemEntity taskItemEntity : taskItemEntityList) {
            JSONObject item = new JSONObject();
            item.putAll(JSONObject.parseObject(taskItemEntity.toString()));
            item.remove("state");
            item.remove("message");
            item.put("loadState", taskItemEntity.getState());
            item.put("loadMessage", taskItemEntity.getMessage());
            TaskRuntimeEntity taskRuntimeEntity = getTaskRuntimeInfo(taskItemEntity);
            if (taskRuntimeEntity != null) {
                item.putAll(JSONObject.parseObject(taskRuntimeEntity.toString()));
                item.remove("state");
                item.remove("message");
                item.put("runState", taskRuntimeEntity.getState());
                item.put("runMessage", taskRuntimeEntity.getMessage());
            }
            jsonArray.add(item);
        }
        response.setData(jsonArray);
        return response;
    }

    @RequestMapping(value = "/stop/{cluster}/{dealSysCode}/{taskName}", method = RequestMethod.PUT)
    public Response stopTask(@PathVariable("cluster") String cluster,
                             @PathVariable("dealSysCode") String dealSysCode,
                             @PathVariable("taskName") String taskName) {
        Response response = new Response().success();
        try {
            String user = (String) SecureUtils.getPrincipal();
            log.info("用户:{}停止任务:{},{},{}", user, cluster, dealSysCode, taskName);
            taskService.stop(taskName);
        } catch (Exception e) {
            log.error("停止任务异常! taskName:{}", taskName, e);
            response.hinderFail("停止任务异常" + e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/start/{cluster}/{dealSysCode}/{taskName}", method = RequestMethod.PUT)
    public Response startTask(@PathVariable("cluster") String cluster,
                              @PathVariable("dealSysCode") String dealSysCode,
                              @PathVariable("taskName") String taskName) {
        Response response = new Response().success();
        try {
            String user = (String) SecureUtils.getPrincipal();
            log.info("用户:{}启动任务:{},{},{}", user, cluster, dealSysCode, taskName);
            taskService.start(taskName);
        } catch (Exception e) {
            log.error("启动任务异常! taskName:{}", taskName, e);
            response.hinderFail("启动任务异常" + e.getMessage());
        }
        return response;
    }

    private TaskRuntimeEntity getTaskRuntimeInfo(TaskItemEntity taskItemEntity) {
        if (taskItemEntity.getWorkerAddress() == null) {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<TaskRuntimeEntity> responseEntity;
        try {
            headers.add(ChronusConstants.API_TOKEN_KEY, SpringContextHolder.getBean(Environment.class).getProperty(ChronusConstants.API_TOKEN_KEY));
            HttpEntity requestEntity = new HttpEntity<>(headers);
            String url = "http://" + taskItemEntity.getWorkerAddress() + "/api/worker/getTaskRuntimeInfo/" + taskItemEntity.getTaskName() + "/" + taskItemEntity.getSeqNo();
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, TaskRuntimeEntity.class);
            if (HttpStatus.OK.equals(responseEntity.getStatusCode()) && responseEntity.getBody() != null) {
                return responseEntity.getBody();
            }
        } catch (Exception e) {
            log.error("获取任务运行信息异常! taskItemEntity:{}", taskItemEntity, e);
        }
        return null;
    }
}
