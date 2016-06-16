package pl.morecraft.dev.stdb.logic;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.morecraft.dev.stdb.command.Command;
import pl.morecraft.dev.stdb.command.CommandType;
import pl.morecraft.dev.stdb.db.Record;
import pl.morecraft.dev.stdb.db.proto.BasicDB;
import pl.morecraft.dev.stdb.misc.IO;
import pl.morecraft.dev.stdb.nio.util.BufferHacker;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

@Component
public class NodeChannelReadConsumer implements BiConsumer<ChannelHandlerContext, String> {

    private final static Logger logger = LoggerFactory.getLogger(NodeChannelReadConsumer.class);

    @Inject
    private BasicDB db;

    @Inject
    private BufferHacker bufferHacker;

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
            return;
        } else if (command.getCommandType() == CommandType.ACK) {
            return;
        } else if (command.getCommandType() == CommandType.EXCEPTION) {
            logger.error(command.getParams()[0]);
            return;
        } else if (command.getCommandType() == CommandType.INSERT
                || command.getCommandType() == CommandType.DELETE
                || command.getCommandType() == CommandType.UPDATE
                || command.getCommandType() == CommandType.SELECT) {
            List<Record> data = db.execute(command);
            command.setData(data.toArray(new Record[data.size()]));
        } else if (command.getCommandType() == CommandType.QUIT) {
            command.setCommandType(CommandType.ACK);
        } else {
            logger.error("Unknown command: {}", request);
            return;
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("Sending message on channel {}: {}, {}", channelHandlerContext.channel().id().asShortText(), command);

        try {
            channelHandlerContext.write(IO.toString(command));
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("Serialization of command was unsuccessful", e);
        }

        if (command.getCommandType() == CommandType.QUIT) {
            channelHandlerContext.channel().closeFuture();
        }
    }

}
