package com.qihoo.finance.chronus.worker.service.impl;

import com.qihoo.finance.chronus.common.exception.ChronusErrorCodeEnum;
import com.qihoo.finance.chronus.common.exception.ServiceException;
import com.qihoo.finance.chronus.common.exception.SystemException;
import com.qihoo.finance.chronus.protocol.exception.NoProviderException;
import com.qihoo.finance.chronus.protocol.exception.TimeoutException;
import com.qihoo.finance.chronus.sdk.ChronusSdkFacade;
import com.qihoo.finance.chronus.sdk.domain.JobData;
import com.qihoo.finance.chronus.worker.log.JobInvokeMonitor;
import com.qihoo.finance.chronus.worker.service.JobDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;

import java.util.List;

/**
 * Created by xiongpu on 2019/1/9.
 */
@Slf4j
public class JobDispatcherImpl implements JobDispatcher {
    protected ChronusSdkFacade chronusSdkFacade;
    protected JobData jobData;

    @Override
    public void init(ChronusSdkFacade chronusSdkFacade, JobData jobData) {
        this.chronusSdkFacade = chronusSdkFacade;
        this.jobData = jobData;
    }

    @Override
    public JobData getJobData() {
        return this.jobData;
    }

    /**
     * 根据条件，查询当前调度服务器可处理的任务
     *
     * @return
     */
    @Override
    @JobInvokeMonitor
    public List selectTasks() {
        try {
            return chronusSdkFacade.selectTasks(jobData);
        } catch (ServiceException e) {
            throw e;
        } catch (RpcException e) {
            if (e.isForbidded()) {
                throw new NoProviderException(ChronusErrorCodeEnum.CALL_BIZ_NO_PROVIDER, e);
            }
            if (e.isTimeout()) {
                throw new TimeoutException(ChronusErrorCodeEnum.CALL_BIZ_TIMEOUT, e);
            }
            throw new SystemException(ChronusErrorCodeEnum.CALL_BIZ_SYS_ERROR, e);
        } catch (Exception e) {
            throw new SystemException(ChronusErrorCodeEnum.CALL_BIZ_SYS_ERROR, e);
        }
    }


    @Override
    @JobInvokeMonitor
    public boolean execute(List taskList) {
        try {
            return chronusSdkFacade.execute(jobData, taskList);
        } catch (ServiceException e) {
            throw e;
        } catch (RpcException e) {
            if (e.isForbidded()) {
                throw new NoProviderException(ChronusErrorCodeEnum.CALL_BIZ_NO_PROVIDER, e);
            }
            if (e.isTimeout()) {
                throw new TimeoutException(ChronusErrorCodeEnum.CALL_BIZ_TIMEOUT, e);
            }
            throw new SystemException(ChronusErrorCodeEnum.CALL_BIZ_SYS_ERROR, e);
        } catch (Exception e) {
            throw new SystemException(ChronusErrorCodeEnum.CALL_BIZ_SYS_ERROR, e);
        }
    }

    /**
     * 根据条件，查询当前调度服务器可处理的任务
     *
     * @return
     */
    @Override
    @JobInvokeMonitor
    public boolean execute() {
        try {
            return chronusSdkFacade.execute(jobData);
        } catch (ServiceException e) {
            throw e;
        } catch (RpcException e) {
            if (e.isForbidded()) {
                throw new NoProviderException(ChronusErrorCodeEnum.CALL_BIZ_NO_PROVIDER, e);
            }
            if (e.isTimeout()) {
                throw new TimeoutException(ChronusErrorCodeEnum.CALL_BIZ_TIMEOUT, e);
            }
            throw new SystemException(ChronusErrorCodeEnum.CALL_BIZ_SYS_ERROR, e);
        } catch (Exception e) {
            throw new SystemException(ChronusErrorCodeEnum.CALL_BIZ_SYS_ERROR, e);
        }
    }
}
