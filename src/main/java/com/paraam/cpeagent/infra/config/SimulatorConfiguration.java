package com.paraam.cpeagent.infra.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2024/11/6 9:53
 */
@Configuration
public class SimulatorConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorConfiguration.class);

    @Value("${simulator.inform.thread:64}")
    private int corePoolSize;

    @Value("${simulator.stun.thread:16}")
    private int stunHeartBeatPoolSize;
    @Value("${simulator.inform.queueCapacity:500000}")
    private int queueCapacity;
    @Bean("bootInformTaskExecutor")
    public TaskExecutor bootInformTaskExecutor() {
        LOGGER.info(">>>>>>>>>>>>>>>>>>>Init Boot Inform Thread Poll.corePoolSize: {}, queueCapacity:{}<<<<<<<<<<<<<<<<<<<<<", corePoolSize, queueCapacity);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(corePoolSize);
        // 设置最大线程数
        executor.setMaxPoolSize(corePoolSize);
        // 设置队列容量
        executor.setQueueCapacity(queueCapacity);
        // 设置允许的空闲时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("thread-bootInform-");
        // 设置拒绝策略rejection-policy：当pool已经达到max size的时候，如何处理新任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return executor;
    }


    @Bean("stunHeartbeatScheduler")
    public TaskScheduler stunHeartbeatScheduler() {
        LOGGER.info(">>>>>>>>>>>>>>>>>>>Init Stun Heart beat Thread Poll. corePoolSize: {}<<<<<<<<<<<<<<<<<<<<<", stunHeartBeatPoolSize);
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(stunHeartBeatPoolSize);
        // 设置默认线程名称
        threadPoolTaskScheduler.setThreadNamePrefix("thread-stun-");
        return threadPoolTaskScheduler;
    }
}
