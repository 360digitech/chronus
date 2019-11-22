package com.qihoo.finance.chronus.modules.job;

import com.qihoo.finance.chronus.sdk.domain.TaskItemDefineDomain;
import com.qihoo.finance.chronus.sdk.service.ChronusSdkSimpleJob;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by xiongpu on 2019/11/15.
 */
@Service("executeBean")
public class ExecuteBean implements ChronusSdkSimpleJob {
    private static final Logger logger = LogManager.getLogger(ExecuteBean.class);

    @Override
    public boolean execute(String taskParameter, List<TaskItemDefineDomain> taskItemList, int eachFetchDataNum) throws Exception {
        System.out.println(taskParameter);
        if (StringUtils.isNotBlank(taskParameter)) {
            Thread.sleep(Long.valueOf(taskParameter.trim()));
        }
        logger.info("结束处理 item:{}，当前时间:{}", taskParameter, LocalDateTime.now());
        return true;
    }
}
