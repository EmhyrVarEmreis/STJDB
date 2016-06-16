package pl.morecraft.dev.stdb.nio.helper;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Sharable
public class CustomHandler extends SimpleChannelInboundHandler<String> {

    private final static Logger logger = LoggerFactory.getLogger(CustomHandler.class);

    private Consumer<ChannelHandlerContext> channelActiveConsumer;
    private BiConsumer<ChannelHandlerContext, String> channelReadConsumer;

    public CustomHandler(Consumer<ChannelHandlerContext> channelActiveConsumer, BiConsumer<ChannelHandlerContext, String> channelReadConsumer) {
        this.channelActiveConsumer = channelActiveConsumer;
        this.channelReadConsumer = channelReadConsumer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channelActive {}", ctx.channel().id().asShortText());
        if (channelActiveConsumer != null) {
            channelActiveConsumer.accept(ctx);
        } else {
            logger.warn("Property channelActiveConsumer is not set!");
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        logger.debug("channelRead0 {}", ctx.channel().id().asShortText());
        if (channelReadConsumer != null) {
            channelReadConsumer.accept(ctx, request);
        } else {
            logger.warn("Property channelReadConsumer is not set!");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.debug("channelReadComplete {}", ctx.channel().id().asShortText());
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("exceptionCaught {" + ctx.channel().id().asShortText() + "}", cause);
        cause.printStackTrace();
        ctx.close();
    }

}
