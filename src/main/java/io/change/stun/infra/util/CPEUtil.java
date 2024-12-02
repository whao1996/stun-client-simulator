package io.change.stun.infra.util;

import java.time.Instant;
import java.util.Date;

import io.change.stun.infra.core.CPEClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class CPEUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CPEUtil.class);

    @Autowired
    @Qualifier("stunHeartbeatScheduler")
    private TaskScheduler stunHeartbeatScheduler;

    public void stunHeartBeatTask(CPEClient cpeClient, Long periodicTime) {
        LOGGER.debug("Add stun heart beat task, time interval is {}", periodicTime);
        stunHeartbeatScheduler.scheduleAtFixedRate(() -> cpeClient.getStunClient().sendBindRequest(),
                Date.from(Instant.now().plusSeconds(periodicTime)),
                periodicTime * 1000L);
    }

}
