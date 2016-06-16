package pl.morecraft.dev.stdb.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.stereotype.Service;
import pl.morecraft.dev.stdb.config.core.Config;
import pl.morecraft.dev.stdb.logic.ServerChannelActiveConsumer;
import pl.morecraft.dev.stdb.logic.ServerChannelReadConsumer;
import pl.morecraft.dev.stdb.nio.helper.CustomHandler;
import pl.morecraft.dev.stdb.nio.helper.CustomInitializer;

import javax.inject.Inject;

@Service
public final class Server {

    @Inject
    private ServerChannelActiveConsumer channelActiveConsumer;

    @Inject
    private ServerChannelReadConsumer channelReadConsumer;

    @Inject
    private Config config;

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new CustomInitializer(new CustomHandler(channelActiveConsumer, channelReadConsumer)));

            Channel channel = b.bind(config.getServerPort()).sync().channel();
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
