package io.change.stun.infra.core.stun;

import io.change.stun.infra.core.CPEClient;
import de.javawi.jstun.attribute.MappedAddress;
import de.javawi.jstun.attribute.MessageAttribute;
import de.javawi.jstun.header.MessageHeader;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StunClientHandler extends SimpleChannelInboundHandler<MessageHeader> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StunClientHandler.class);

    private final CPEClient cpeClient;

    public StunClientHandler(CPEClient cpeClient) {
        this.cpeClient = cpeClient;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, MessageHeader msg) {
        MappedAddress mappedAddress = (MappedAddress) msg.getMessageAttribute(MessageAttribute.MessageAttributeType.MappedAddress);
        LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>Received Mapped Address: {}:{}<<<<<<<<<<<<<<<<<<<<<<<", mappedAddress.getAddress(), mappedAddress.getPort());
    }
}
