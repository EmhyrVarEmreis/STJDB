package pl.morecraft.dev.stdb.logic;

import io.netty.channel.ChannelHandlerContext;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pl.morecraft.dev.stdb.command.Command;
import pl.morecraft.dev.stdb.command.CommandType;
import pl.morecraft.dev.stdb.logic.registry.NodeChannelRegistry;
import pl.morecraft.dev.stdb.misc.IO;
import pl.morecraft.dev.stdb.nio.util.BufferHacker;
import pl.morecraft.dev.stdb.nio.util.ClientSender;

import javax.inject.Inject;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ServerChannelReadConsumer implements BiConsumer<ChannelHandlerContext, String> {

    private final static Logger logger = LoggerFactory.getLogger(ServerChannelReadConsumer.class);

    @Inject
    private NodeChannelRegistry nodeChannelRegistry;

    @Inject
    private BufferHacker bufferHacker;

    @Inject
    private ClientSender clientSender;

    @Inject
    @Qualifier("defaultDateTimeFormatter")
    public DateTimeFormatter dateTimeFormatter;

    @SuppressWarnings("Duplicates")
    @Override
    public void accept(ChannelHandlerContext channelHandlerContext, String request) {
        logger.info("Received message on channel {}: {}", channelHandlerContext.channel().id().asShortText(), request);

        Command command;
        try {
            command = (Command) IO.fromString(bufferHacker.getData() + request);
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            if (request.length() == 1024) {
                logger.warn("Received 1024 chars of data and it cannot be deserialized; Trying to append next parts");
                bufferHacker.append(request);
                return;
            }
            logger.error("Deserialization of command was unsuccessful; Aborting execution", e);
            command = new Command(CommandType.EXCEPTION, new String[]{e.getMessage()});
            try {
                channelHandlerContext.channel().writeAndFlush(IO.toString(command));
            } catch (IOException ee) {
                //e.printStackTrace();
                logger.error("Serialization of command was unsuccessful", ee);
            }
            return;
        }

        bufferHacker.clear();

        logger.info("Deserialized message: {}", command);

        if (command.getCommandType() == CommandType.WELCOME) {
            nodeChannelRegistry.put(channelHandlerContext.channel());
            return;
        }

        if (command.getData() != null) {
            clientSender.send(
                    command,
                    "--- BEGIN RESULT ---\r\n"
                            + Stream.of(command.getData()).filter(record -> record != null).map(record -> record.toPrintString(dateTimeFormatter)).collect(Collectors.joining("\r\n"))
                            + "\r\n--- END RESULT ---\r\n\r\n"
            );
        }

    }

}
