package pl.morecraft.dev.stdb.logic;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.morecraft.dev.stdb.command.Command;
import pl.morecraft.dev.stdb.command.CommandType;
import pl.morecraft.dev.stdb.command.InvalidCommandException;
import pl.morecraft.dev.stdb.command.SharedCommandQueue;
import pl.morecraft.dev.stdb.logic.registry.ClientChannelRegistry;

import javax.inject.Inject;
import java.util.function.BiConsumer;

@Component
public class ReceiverChannelReadConsumer implements BiConsumer<ChannelHandlerContext, String> {

    private final static Logger logger = LoggerFactory.getLogger(ReceiverChannelReadConsumer.class);

    @Inject
    private SharedCommandQueue queue;

    @Inject
    private ClientChannelRegistry channelRegistry;

    @Override
    public void accept(ChannelHandlerContext channelHandlerContext, String request) {
        request = request.replace("\n", " ").replace("\r", " ").trim();

        logger.info("Received command on channel {}: {}", channelHandlerContext.channel().id().asShortText(), request);

        channelRegistry.put(channelHandlerContext.channel());

        Command command;
        try {
            command = Command.valueOf(request, channelHandlerContext.channel().id().asShortText());
        } catch (InvalidCommandException e) {
            logger.error("Invalid command: {}", request);
            channelHandlerContext.write("Invalid command\r\n\r\n");
            return;
        }

        if (command.getCommandType() == CommandType.EXIT) {
            logger.info("Closing connection: {}", channelHandlerContext.channel().id().asShortText());
            channelRegistry.remove(channelHandlerContext.channel());
            channelHandlerContext.writeAndFlush("Closing...\r\n");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                logger.error("Process cannot wait", e);
            }
            channelHandlerContext.writeAndFlush("Bye!\r\n");
            channelHandlerContext.channel().close();
        } else {
            queue.add(command);
            channelHandlerContext.write("Processing...\r\n");
        }
    }

}
