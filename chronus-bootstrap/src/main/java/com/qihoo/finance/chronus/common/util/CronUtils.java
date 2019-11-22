package com.qihoo.finance.chronus.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.util.Date;

/**
 * Created by xiongpu on 2019/5/31.
 */
@Slf4j
public class CronUtils {

    public static Date getNextDateAfterNow(String cronExpression, Date now) {
        try {
            CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronExpression);
            Date tmpDate = cronSequenceGenerator.next(now);
            return tmpDate;
        } catch (Exception e) {
            log.error("获取下次执行时间异常", e);
            throw e;
        }
    }
}
