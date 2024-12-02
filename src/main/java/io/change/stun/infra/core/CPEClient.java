package io.change.stun.infra.core;

import io.change.stun.infra.core.stun.StunClient;
import io.change.stun.infra.properties.STUNConfig;
import io.change.stun.infra.util.ApplicationContextHelper;
import io.change.stun.infra.util.CPEUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CPEClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CPEClient.class);
    private STUNConfig stunConfig;
    private StunClient stunClient;


    public StunClient getStunClient() {
        return stunClient;
    }

    public void setStunClient(StunClient stunClient) {
        this.stunClient = stunClient;
    }

    public STUNConfig getStunConfig() {
        return stunConfig;
    }

    public void setStunConfig(STUNConfig stunConfig) {
        this.stunConfig = stunConfig;
    }

    public void bootstrap() {
        StunClient stunClient = new StunClient(stunConfig, this);
        this.setStunClient(stunClient);
        stunClient.connect();
        stunClient.sendBindRequest();
        ApplicationContextHelper.getBean(CPEUtil.class)
                .stunHeartBeatTask(this,
                        getStunConfig().getHeartbeatInterval());
    }
}
