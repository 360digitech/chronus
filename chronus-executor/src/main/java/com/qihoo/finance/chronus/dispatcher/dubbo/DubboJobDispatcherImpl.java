package com.qihoo.finance.chronus.dispatcher.dubbo;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.util.BeanUtils;
import com.qihoo.finance.chronus.dispatcher.TaskBatchDealService;
import com.qihoo.finance.chronus.dispatcher.TaskSelectTaskService;
import com.qihoo.finance.chronus.dispatcher.TaskSimpleDealService;
import com.qihoo.finance.chronus.dispatcher.TaskSingleDealService;
import com.qihoo.finance.chronus.metadata.api.task.bo.TaskItemDefine;
import com.qihoo.finance.chronus.sdk.ChronusSdkProcessor;
import com.qihoo.finance.chronus.sdk.domain.TaskItemDefineDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.List;

import static com.qihoo.finance.chronus.common.exception.ChronusErrorCodeEnum.CALL_EXT_SYS_ERROR;

/**
 * Created by xiongpu on 2018/6/29.
 */
@Slf4j
public class DubboJobDispatcherImpl extends AbstractDubboJobDispatcher implements TaskSingleDealService,TaskBatchDealService,TaskSelectTaskService,TaskSimpleDealService {

    public DubboJobDispatcherImpl(ChronusSdkProcessor chronusSdkFacade) {
        super(chronusSdkFacade);
    }

    /**
     * 根据条件，查询当前调度服务器可处理的任务
     *
     * @param taskParameter    任务的自定义参数
     * @param taskItemList     当前调度服务器，分配到的可处理队列
     * @param eachFetchDataNum 每次获取数据的数量
     * @return
     */
    @Override
    public List selectTasks(String taskParameter, List taskItemList, int eachFetchDataNum) {
        StopWatch clock = getStopWatchAndStart();
        boolean resultFlag = true;

        List<TaskItemDefineDomain> taskItemDefineDomainList = taskItemList == null ? null : BeanUtils.copyBeanList(TaskItemDefineDomain.class, taskItemList);
        getLogger().info("开始获取分发结果集selectTasks，当前时间:{}, jobConfig:{}  taskItemDefineDomainList:{}  eachFetchDataNum:{}", LocalDateTime.now(), jobConfig, taskItemDefineDomainList, eachFetchDataNum);
        Response<List> response = new Response<>();
        try {
            List data = chronusSdkFacade.selectTasks(jobConfig, taskItemDefineDomainList, eachFetchDataNum);
            response.setData(data);
        } catch (Exception e) {
            resultFlag = false;
            response.fail(CALL_EXT_SYS_ERROR.getCode(), CALL_EXT_SYS_ERROR.getMsg() + e.getMessage());
            log.error("获取到分发结果集异常 jobConfig:{}", jobConfig, e);
        } finally {
            getLogger().info("获取到分发结果集selectTasks，当前时间:{}, response:{}", LocalDateTime.now(), response);
            printSelectTaskMonitorLog(clock, CollectionUtils.isEmpty(response.getData()) ? 0 : response.getData().size(), resultFlag);
        }

        if (response.failed()) {
            log.error("selectTasks error jobConfig:{} response :{}", jobConfig, response);
        }
        return response.getData();
    }

    @Override
    public boolean execute(Object item) {
        StopWatch clock = getStopWatchAndStart();
        getLogger().info("DUBBO JOB 开始处理execute，taskParameter:{} 执行项：{}， 当前时间：{}", jobConfig.getTaskParameter(), item, LocalDateTime.now());
        boolean resultFlag;
        try {
            resultFlag = chronusSdkFacade.execute(jobConfig, item);
        } catch (Exception e) {
            getLogger().error("调度异常 {} item:{} jobConfig:{}", jobConfig.getSysCode(), item, jobConfig, e);
            resultFlag = false;
        }
        getLogger().info("结束本次 execute，item:{} resultFlag:{} 当前时间:{}", item, resultFlag, LocalDateTime.now());
        printExecMonitorLog(clock, resultFlag);
        return resultFlag;
    }

    @Override
    public boolean execute(Object[] taskList) {
        StopWatch clock = getStopWatchAndStart();
        getLogger().info("DUBBO JOB 开始处理executeBatch，taskParameter:{} 执行项：{}， 当前时间：{}", jobConfig.getTaskParameter(), taskList, LocalDateTime.now());
        boolean resultFlag;
        try {
            resultFlag = chronusSdkFacade.executeBatch(jobConfig, taskList);
        } catch (Exception e) {
            getLogger().error("调度异常 {} item:{} jobConfig:{}", jobConfig.getSysCode(), taskList, jobConfig, e);
            resultFlag = false;
        }
        getLogger().info("结束本次 executeBatch, item:{} resultFlag:{} 当前时间:{}", taskList, resultFlag, LocalDateTime.now());
        printExecMonitorLog(clock, resultFlag);
        return resultFlag;
    }

    /**
     * 根据条件，查询当前调度服务器可处理的任务
     *
     * @param taskParameter    任务的自定义参数
     * @param taskItemList     当前调度服务器，分配到的可处理队列
     * @param eachFetchDataNum 每次获取数据的数量
     * @return
     */
    @Override
    public boolean execute(String taskParameter, List<TaskItemDefine> taskItemList, int eachFetchDataNum) {
        StopWatch clock = getStopWatchAndStart();
        boolean resultFlag = true;
        List<TaskItemDefineDomain> taskItemDefineDomainList = CollectionUtils.isEmpty(taskItemList) ? null : BeanUtils.copyBeanList(TaskItemDefineDomain.class, taskItemList);
        getLogger().info("开始执行任务execute，当前时间:{}, jobConfig:{}  taskItemDefineDomainList:{}  eachFetchDataNum:{}", LocalDateTime.now(), jobConfig, taskItemDefineDomainList, eachFetchDataNum);

        try {
            resultFlag = chronusSdkFacade.execute(jobConfig, taskItemDefineDomainList, eachFetchDataNum);
        } catch (Exception e) {
            resultFlag = false;
            log.error("执行任务异常 jobConfig:{}", jobConfig, e);
        } finally {
            getLogger().info("执行任务execute，当前时间:{}, resultFlag:{}", LocalDateTime.now(), resultFlag);
            printExecMonitorLog(clock, resultFlag);
        }
        return resultFlag;
    }
}
