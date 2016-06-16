package pl.morecraft.dev.stdb.nio;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.stereotype.Service;
import pl.morecraft.dev.stdb.config.core.Config;
import pl.morecraft.dev.stdb.logic.NodeChannelActiveConsumer;
import pl.morecraft.dev.stdb.logic.NodeChannelReadConsumer;
import pl.morecraft.dev.stdb.nio.helper.CustomHandler;
import pl.morecraft.dev.stdb.nio.helper.CustomInitializer;

import javax.inject.Inject;

@Service
public final class Node {

    @Inject
    private NodeChannelActiveConsumer channelActiveConsumer;

    @Inject
    private NodeChannelReadConsumer channelReadConsumer;

    @Inject
    private Config config;

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new CustomInitializer(new CustomHandler(channelActiveConsumer, channelReadConsumer)));

            ChannelFuture channelFuture = b.connect(config.getServerHost(), config.getServerPort()).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

}
