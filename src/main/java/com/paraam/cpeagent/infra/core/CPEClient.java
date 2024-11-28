package com.paraam.cpeagent.infra.core;

import com.paraam.cpeagent.infra.core.stun.StunClient;
import com.paraam.cpeagent.infra.properties.STUNConfig;
import com.paraam.cpeagent.infra.util.ApplicationContextHelper;
import com.paraam.cpeagent.infra.util.CPEUtil;
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

        if (Boolean.TRUE.equals(this.stunConfig.getEnabled())) {
            StunClient stunClient = new StunClient(stunConfig, this);
            this.setStunClient(stunClient);
            stunClient.connect();
            stunClient.sendBindRequest();
            ApplicationContextHelper.getBean(CPEUtil.class)
                    .stunHeartBeatTask(this,
                            getStunConfig().getHeartbeatInterval());
        }
    }
}
