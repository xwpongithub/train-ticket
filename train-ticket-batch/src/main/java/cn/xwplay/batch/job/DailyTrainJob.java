package cn.xwplay.batch.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.xwplay.batch.feign.BusinessFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;

import java.util.Date;

@DisallowConcurrentExecution
@Slf4j
@RequiredArgsConstructor
public class DailyTrainJob implements Job {

    private final BusinessFeign businessFeign;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        log.info("生成15天后的车次数据开始");
        var date = new Date();
        var dateTime = DateUtil.offsetDay(date, 15);
        var offsetDate = dateTime.toJdkDate();
        var commonResp = businessFeign.genDaily(offsetDate);
        log.info("生成15天后的车次数据结束，结果：{}", commonResp);
    }
}
