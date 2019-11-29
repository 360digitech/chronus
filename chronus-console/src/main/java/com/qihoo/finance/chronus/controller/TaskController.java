package com.qihoo.finance.chronus.controller;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.util.ControllerUtil;
import com.qihoo.finance.chronus.core.task.service.TaskRuntimeService;
import com.qihoo.finance.chronus.core.task.service.TaskService;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;


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
    private TaskRuntimeService taskRuntimeService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Response insert(@RequestBody @Valid TaskEntity taskEntity, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        try {
            if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
                return response;
            }
            String userName = (String) SecurityUtils.getSubject().getPrincipal();
            taskEntity.setCreatedBy(userName);
            taskEntity.setUpdatedBy(userName);
            taskService.insert(taskEntity);
        } catch (Exception e) {
            log.error("新增任务异常! tagEntity:{}", taskEntity, e);
            response.hinderFail("新增任务异常" + e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/{taskName}", method = RequestMethod.DELETE)
    public Response delete(@PathVariable("taskName") String taskName) throws Exception {
        Response response = new Response().success();
        try {
            //TODO 校验
            taskService.delete(taskName);
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
            String userName = (String) SecurityUtils.getSubject().getPrincipal();
            taskEntity.setUpdatedBy(userName);
            taskEntity.setDateUpdated(new Date());
            taskService.update(taskEntity);
        } catch (Exception e) {
            log.error("更新任务异常! tagEntity:{}", taskEntity, e);
            response.hinderFail("更新任务异常" + e.getMessage());
        }
        return response;
    }


    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public Response detail(TaskEntity taskEntity) throws Exception {
        Response response = new Response().success();
        taskService.selectById(taskEntity);
        return response;
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.POST)
    public Response getAllTask(@RequestBody TaskEntity taskEntity) throws Exception {
        Response response = new Response().success();
        PageResult<TaskEntity> pageResult = taskService.selectListByPage(taskEntity);
        response.setData(pageResult);
        return response;
    }


    @RequestMapping(value = "/sync", method = RequestMethod.PUT)
    public Response syncTask(String cluster, TaskEntity taskEntity) throws Exception {
        Response response = new Response().success();
        taskEntity.setId(null);
        taskEntity.setCluster(cluster);
        String userName = (String) SecurityUtils.getSubject().getPrincipal();
        Date date = new Date();
        taskEntity.setCreatedBy(userName);
        taskEntity.setUpdatedBy(userName);
        taskEntity.setDateCreated(date);
        taskEntity.setDateUpdated(date);
        taskService.insert(taskEntity);
        return response;
    }


    @RequestMapping(value = "/taskRuntime", method = RequestMethod.POST)
    public Response taskRuntime(@RequestBody TaskEntity taskEntity) throws Exception {
        Response response = new Response().success();
        List<TaskRuntimeEntity> taskRuntimeEntityList = taskRuntimeService.selectTaskRuntimeByTaskName(taskEntity.getCluster(), taskEntity.getTaskName(), taskEntity.getJudgeDeadInterval());
        response.setData(taskRuntimeEntityList);
        return response;
    }

    @RequestMapping(value = "/taskStatus", method = RequestMethod.PUT)
    public Response taskResume(@RequestBody TaskEntity taskEntity) throws Exception {
        Response response = new Response().success();
        String userName = (String) SecurityUtils.getSubject().getPrincipal();
        Date date = new Date();
        taskEntity.setUpdatedBy(userName);
        taskEntity.setDateUpdated(date);
        taskService.update(taskEntity);
        return response;
    }

}
