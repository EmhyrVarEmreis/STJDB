package pl.morecraft.dev.stdb.logic;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.morecraft.dev.stdb.command.Command;
import pl.morecraft.dev.stdb.command.CommandType;
import pl.morecraft.dev.stdb.misc.IO;

import java.io.IOException;
import java.util.function.Consumer;

@Component
public class NodeChannelActiveConsumer implements Consumer<ChannelHandlerContext> {

    private final static Logger logger = LoggerFactory.getLogger(NodeChannelActiveConsumer.class);

    @Override
    public void accept(ChannelHandlerContext channelHandlerContext) {
        Command command = new Command(CommandType.WELCOME, null);
        logger.info("Sending message on channel {}: {}, {}", channelHandlerContext.channel().id().asShortText(), command);

        try {
            channelHandlerContext.writeAndFlush(IO.toString(command));
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("Serialization of command was unsuccessful", e);
        }
    }

}
