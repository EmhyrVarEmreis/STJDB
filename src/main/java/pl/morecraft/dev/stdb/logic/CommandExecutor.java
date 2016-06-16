package pl.morecraft.dev.stdb.logic;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.morecraft.dev.stdb.command.Command;
import pl.morecraft.dev.stdb.command.CommandType;
import pl.morecraft.dev.stdb.command.SharedCommandQueue;
import pl.morecraft.dev.stdb.config.core.Config;
import pl.morecraft.dev.stdb.db.Record;
import pl.morecraft.dev.stdb.logic.registry.NodeChannelRegistry;
import pl.morecraft.dev.stdb.logic.registry.NodeDistributor;
import pl.morecraft.dev.stdb.logic.registry.ObjectRegistry;
import pl.morecraft.dev.stdb.misc.IO;
import pl.morecraft.dev.stdb.nio.util.ClientSender;

import javax.inject.Inject;
import java.io.IOException;

import static pl.morecraft.dev.stdb.misc.Help.HELP_CMD;

@Service
public class CommandExecutor {

    private final static Logger logger = LoggerFactory.getLogger(CommandExecutor.class);

    @Inject
    private SharedCommandQueue queue;

    @Inject
    private Config config;

    @Inject
    private NodeChannelRegistry nodeChannelRegistry;

    @Inject
    private NodeDistributor nodeDistributor;

    @Inject
    private ClientSender clientSender;

    @Inject
    private ObjectRegistry objectRegistry;

    public void run() throws InterruptedException {

        for (; ; ) {

            nodeChannelRegistry.refresh();

            if (config.getConnectionCount() != nodeChannelRegistry.getChannelIDs().size()) {
                logger.info("Waiting for ALL nodes; Connected: {}; Needed: {}; Sleep 3 seconds", config.getConnectionCount(), nodeChannelRegistry.getChannelIDs().size());
                Thread.sleep(3000);
                continue;
            }

            Command command = queue.pool();

            try {
                if (command == null) {
                    Thread.sleep(200);
                } else if (command.getCommandType() == CommandType.QUIT) {
                    break;
                } else if (command.getCommandType() == CommandType.HELP) {
                    logger.info("Sending help");
                    clientSender.send(
                            command.getSourceChannelId(),
                            HELP_CMD
                    );
                } else if (command.getCommandType() == CommandType.INSERT) {
                    Channel channel = nodeDistributor.getNext();
                    if (channel == null) {
                        logger.info("Got NULL channel; Waiting 3 seconds and putting command [{}] back to stack", command);
                        Thread.sleep(3000);
                        queue.add(command);
                    } else {
                        JsonElement jsonElement = new JsonParser().parse(command.getParams()[1]);
                        jsonElement = jsonElement.getAsJsonObject().get(command.getParams()[0]);
                        if (jsonElement == null) {
                            logger.warn("Cannot retrieve key {} from JSON object: {}", command.getParams()[0], command.getParams()[1]);
                            clientSender.send(command.getSourceChannelId(), "Invalid key!\r\nCommand Aborted!\r\n\r\n");
                            continue;
                        }
                        String id = jsonElement.getAsString();

                        if (objectRegistry.get(id) != null) {
                            logger.warn("Cannot INSERT key {} because of duplicate", id);
                            clientSender.send(command.getSourceChannelId(), "Key duplicated!\r\nUse UPDATE instead!\r\n\r\n");
                            continue;
                        }

                        objectRegistry.put(id, channel.id().asShortText());

                        Record record = new Record(id, command.getParams()[1]);

                        command.setNodeChannelId(channel.id().asShortText());
                        command.setData(new Record[]{record});

                        logger.info("Sending command on channel {}: {}", channel.id().asShortText(), command);

                        try {
                            channel.writeAndFlush(IO.toString(command));
                        } catch (IOException e) {
                            //e.printStackTrace();
                            logger.error("Serialization of command was unsuccessful", e);
                        }
                    }
                } else if (command.getCommandType() == CommandType.DELETE
                        || command.getCommandType() == CommandType.SELECT) {
                    String channelId;
                    if ((channelId = objectRegistry.get(command.getParams()[0])) == null) {
                        logger.warn("Cannot perform {} operation because key {} does not exist in DB", command.getCommandType().toString(), command.getParams()[0]);
                        clientSender.send(command.getSourceChannelId(), "The specified key does not exist!\r\nCannot perform operation!\r\n\r\n");
                        continue;
                    }
                    Channel channel = nodeChannelRegistry.get(channelId);

                    if (command.getCommandType() == CommandType.DELETE) {
                        objectRegistry.remove(command.getParams()[0]);
                    }

                    command.setNodeChannelId(channel.id().asShortText());
                    try {
                        channel.writeAndFlush(IO.toString(command));
                    } catch (IOException e) {
                        //e.printStackTrace();
                        logger.error("Serialization of command was unsuccessful", e);
                    }
                } else if (command.getCommandType() == CommandType.UPDATE) {
                    JsonElement jsonElement = new JsonParser().parse(command.getParams()[1]);
                    jsonElement = jsonElement.getAsJsonObject().get(command.getParams()[0]);
                    if (jsonElement == null) {
                        logger.warn("Cannot retrieve key {} from JSON object: {}", command.getParams()[0], command.getParams()[1]);
                        clientSender.send(command.getSourceChannelId(), "Invalid key!\r\nCommand Aborted!\r\n\r\n");
                        continue;
                    }
                    String id = jsonElement.getAsString();

                    String channelId;
                    if ((channelId = objectRegistry.get(id)) == null) {
                        logger.warn("Cannot UPDATE key {} because key does not exist in DB", id);
                        clientSender.send(command.getSourceChannelId(), "The specified key does not exist!\r\nUse INSERT instead!\r\n\r\n");
                        continue;
                    }
                    Channel channel = nodeChannelRegistry.get(channelId);

                    Record record = new Record(id, command.getParams()[1]);

                    command.setNodeChannelId(channel.id().asShortText());
                    command.setData(new Record[]{record});

                    try {
                        channel.writeAndFlush(IO.toString(command));
                    } catch (IOException e) {
                        //e.printStackTrace();
                        logger.error("Serialization of command was unsuccessful", e);
                    }
                }
            } catch (Exception e) {
                logger.error("An unexpected exception caught; Command will be lost;", e);
            }

            Thread.sleep(200);

        }

    }

}
