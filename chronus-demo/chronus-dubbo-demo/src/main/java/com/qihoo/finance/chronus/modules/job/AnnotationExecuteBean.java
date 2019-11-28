package com.qihoo.finance.chronus.modules.job;

import com.qihoo.finance.chronus.sdk.annotation.Job;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Created by xiongpu on 2019/11/15.
 */
@Service("annotationExecuteBean")
public class AnnotationExecuteBean {
    private static final Logger logger = LogManager.getLogger(AnnotationExecuteBean.class);

    @Job(key = "annotationExecuteBean.execute1")
    public boolean execute(String taskParameter) throws Exception {
        System.out.println(taskParameter);
        if (StringUtils.isNotBlank(taskParameter)) {
            Thread.sleep(Long.valueOf(taskParameter.trim()));
        }
        logger.info("结束处理 item:{}，当前时间:{}", taskParameter, LocalDateTime.now());
        return true;
    }

    @Job(key = "annotationExecuteBean.execute2")
    public boolean execute(String taskParameter, int eachFetchDataNum) throws Exception {
        System.out.println(taskParameter);
        if (StringUtils.isNotBlank(taskParameter)) {
            Thread.sleep(Long.valueOf(taskParameter.trim()));
        }
        logger.info("结束处理 item:{}，当前时间:{}", taskParameter, LocalDateTime.now());
        return true;
    }


    @Job(key = "annotationExecuteBean.execute3")
    public boolean execute() throws Exception {
        logger.info("结束处理 当前时间:{}", LocalDateTime.now());
        return true;
    }
}
