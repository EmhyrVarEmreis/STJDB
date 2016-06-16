package pl.morecraft.dev.stdb.logic;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.function.Consumer;

@Component
public class ReceiverChannelActiveConsumer implements Consumer<ChannelHandlerContext> {

    private final static Logger logger = LoggerFactory.getLogger(NodeChannelActiveConsumer.class);

    @Inject
    @Qualifier("greeting")
    private String greeting;

    @Override
    public void accept(ChannelHandlerContext channelHandlerContext) {
        logger.info("Sending greeting on channel {}: {}", channelHandlerContext.channel().id().asShortText(), greeting.replace("\n", " ").replace("\r", " ").trim());
        channelHandlerContext.writeAndFlush(greeting);
    }

}
