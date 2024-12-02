package io.change.stun.infra.core.stun;

import de.javawi.jstun.header.MessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

public class StunResponseDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        ByteBuf buffer = msg.content();
        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);

        MessageHeader messageHeader = MessageHeader.parseHeader(data);
        messageHeader.parseAttributes(data);
        out.add(messageHeader);
    }
}
