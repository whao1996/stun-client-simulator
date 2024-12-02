package io.change.stun.infra.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
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

    @Value("${simulator.stun.thread:16}")
    private int stunHeartBeatPoolSize;

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
