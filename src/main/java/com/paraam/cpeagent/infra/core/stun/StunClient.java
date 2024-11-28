package com.paraam.cpeagent.infra.core.stun;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.paraam.cpeagent.infra.core.CPEClient;
import com.paraam.cpeagent.infra.properties.STUNConfig;
import de.javawi.jstun.attribute.ChangeRequest;
import de.javawi.jstun.attribute.ConnectionRequestBinding;
import de.javawi.jstun.attribute.Password;
import de.javawi.jstun.attribute.Username;
import de.javawi.jstun.header.MessageHeader;
import de.javawi.jstun.util.UtilityException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StunClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(StunClient.class);
    // 创建事件循环组
    private static final NioEventLoopGroup GROUP;
    private STUNConfig stunConfig;

    private CPEClient cpeClient;
    private Channel channel = null;

    static {
        Integer DEFAULT_EVENT_LOOP_THREADS = NettyRuntime.availableProcessors() * 2;
        String stunThread = System.getenv("NETTY_THREAD");
        if (stunThread != null) {
            DEFAULT_EVENT_LOOP_THREADS = Integer.parseInt(stunThread);
        }
        LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>netty event loop thread is : {}<<<<<<<<<<<<<<<<<<", DEFAULT_EVENT_LOOP_THREADS);
        GROUP = new NioEventLoopGroup(DEFAULT_EVENT_LOOP_THREADS);
    }

    public StunClient() {
    }

    public StunClient(STUNConfig stunConfig, CPEClient cpeClient) {
        this.stunConfig = stunConfig;
        this.cpeClient = cpeClient;
    }

    public int connect() {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(GROUP)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true) // 允许广播
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        public void initChannel(DatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new StunResponseDecoder());
                            ch.pipeline().addLast(new StunClientHandler(cpeClient));
                        }
                    });
            ChannelFuture future = bootstrap
                    //                    .bind(65530).sync().channel()
                    .connect(new InetSocketAddress(stunConfig.getHost(), stunConfig.getPort())).sync();
            channel = future.channel();
            InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.localAddress();
            return inetSocketAddress.getPort();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendBindRequest() {
        if (channel != null) {
            byte[] stunRequest = createStunRequest();
            LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>>>Send binding Request<<<<<<<<<<<<<<<<<<<<<<<<<<");
            // 向服务器发送 STUN 请求
            channel.writeAndFlush(Unpooled.copiedBuffer(stunRequest));
        }
    }

    // 创建 STUN Binding Request 请求报文
    private byte[] createStunRequest() {
        MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
        try {
            sendMH.generateTransactionID();
            ChangeRequest changeRequest = new ChangeRequest();
            ConnectionRequestBinding connectionRequestBinding = new ConnectionRequestBinding();
            connectionRequestBinding.setData("dslforum.org/TR-111 ".getBytes(StandardCharsets.UTF_8));

            sendMH.addMessageAttribute(changeRequest);
            sendMH.addMessageAttribute(connectionRequestBinding);

            if (StringUtils.isNoneBlank(stunConfig.getUsername())) {
                Username username = new Username(stunConfig.getUsername());
                sendMH.addMessageAttribute(username);
            }
            if (StringUtils.isNoneBlank(stunConfig.getPassword())) {
                Password password = new Password(stunConfig.getPassword());
                sendMH.addMessageAttribute(password);
            }

            byte[] data = sendMH.getBytes();
            return data;
        } catch (UtilityException e) {
            throw new RuntimeException(e);
        }
    }
}
