package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.PageQueryParams;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.log.dao.JobExecLogDao;
import com.qihoo.finance.chronus.metadata.api.log.entity.JobExecLogEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by xiongpu on 2019/7/29.
 */
public class JobExecLogMongoDBDaoImpl extends AbstractMongoBaseDao<JobExecLogEntity> implements JobExecLogDao {
    public JobExecLogMongoDBDaoImpl(String collectionName, @Autowired MongoTemplate mongoTemplate) {
        super(mongoTemplate, collectionName);
    }

    @Override
    public void batchInsert(List<JobExecLogEntity> jobExecLogList) {
        super.insert(jobExecLogList);
    }

    @Override
    public List<JobExecLogEntity> selectListAll() {
        return super.selectListAll();
    }

    @Override
    public List<JobExecLogEntity> selectListByCluster(String cluster, String taskName, String sysCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("cluster").is(cluster));
        query.addCriteria(Criteria.where("taskName").is(taskName));
        query.addCriteria(Criteria.where("sysCode").is(sysCode));
        return super.selectList(query);
    }

    @Override
    public PageResult<JobExecLogEntity> findAllByPage(JobExecLogEntity jobExecLogEntity, List<String> sysCodes) {
        int page = jobExecLogEntity.getPageNum() - 1;
        PageQueryParams pageQueryParams = new PageQueryParams(page, jobExecLogEntity.getPageSize());
        Query query = new Query();
        query.addCriteria(sysCodes.contains(jobExecLogEntity.getSysCode())
                ? Criteria.where("sysCode").is(jobExecLogEntity.getSysCode())
                : Criteria.where("sysCode").in(sysCodes));
        if (StringUtils.isNotEmpty(jobExecLogEntity.getCluster())) {
            query.addCriteria(Criteria.where("cluster").is(jobExecLogEntity.getCluster()));
        }
        if (StringUtils.isNotEmpty(jobExecLogEntity.getTaskName())) {
            query.addCriteria(Criteria.where("taskName").regex(".*?" + jobExecLogEntity.getTaskName() + ".*", "i"));
        }
        if (jobExecLogEntity.getStartDate() != null) {
            //使用调度的开始时间判断
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar.setTime(jobExecLogEntity.getStartDate());
            Date startDate = calendar.getTime();
            if (jobExecLogEntity.getEndDate() != null) {
                calendar.setTime(jobExecLogEntity.getEndDate());
                Date endDate = calendar.getTime();
                query.addCriteria(Criteria.where("startDate").gte(startDate).andOperator(Criteria.where("startDate").lte(endDate)));
            } else {
                query.addCriteria(Criteria.where("startDate").gte(startDate));
            }
        }
        Long count = this.countByQuery(query);
        query.with(new Sort(Sort.Direction.DESC, "startDate"));
        return super.generatePageResult(count, jobExecLogEntity.getPageSize(), page, this.selectByQuery(query, pageQueryParams));
    }

    @Override
    public void delete(String taskName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("taskName").is(taskName));
        super.delete(query);
    }
}
