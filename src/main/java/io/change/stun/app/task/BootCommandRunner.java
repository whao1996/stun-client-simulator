package io.change.stun.app.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import io.change.stun.infra.core.CPEClient;
import io.change.stun.infra.properties.STUNConfig;
import io.netty.util.NettyRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2024/10/25 9:54
 */
@Component
public class BootCommandRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(BootCommandRunner.class);

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR;

    @Value("${simulator.device_number}")
    private Integer deviceNumber;

    @Autowired
    private STUNConfig stunConfig;

    static {
        Integer BOOT_THREADS = NettyRuntime.availableProcessors() * 2;
        String stunThread = System.getenv("BOOT_THREAD");
        if (stunThread != null) {
            BOOT_THREADS = Integer.parseInt(stunThread);
        }
        Integer QUEUE_CAPACITY = 1000000;
        String stunThreadStr = System.getenv("QUEUE_CAPACITY");
        if (stunThreadStr != null) {
            BOOT_THREADS = Integer.parseInt(stunThreadStr);
        }
        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(BOOT_THREADS,
                BOOT_THREADS,
                60,
                java.util.concurrent.TimeUnit.SECONDS,
                new java.util.concurrent.ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.AbortPolicy());
    }
    @Override
    public void run(String... args) {
        List<CPEClient> cpeClientList = new ArrayList<>();
        for (int i = 0; i < deviceNumber; i++) {
            CPEClient cpeClient = new CPEClient();
            cpeClient.setStunConfig(stunConfig);
            cpeClientList.add(cpeClient);
        }

        CountDownLatch countDownLatch = new CountDownLatch(cpeClientList.size());
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.info(">>>>>>>>>>>>>>>>>>>Start Boot CPE device: Device count: {}<<<<<<<<<<<<<<<<<<<<<", cpeClientList.size());
        // 初始化设备
        for (CPEClient cpeClient : cpeClientList) {
            THREAD_POOL_EXECUTOR.execute(() -> {
                try {
                    cpeClient.bootstrap();
                    successCount.getAndIncrement();
                } catch (Exception e) {
                    LOGGER.error("Init CPE client failed.", e);
                    failedCount.getAndIncrement();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        // 等待设备启动完成
        try {
            countDownLatch.await();
            stopWatch.stop();
            LOGGER.info(">>>>>>>>>>>>>>>>>>>End Boot CPE device: Success count: {},failed count: {}, cost time: {}s<<<<<<<<<<<<<<<<<<<<<", successCount.get(), failedCount.get(), stopWatch.getTotalTimeSeconds());
            THREAD_POOL_EXECUTOR.shutdown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
