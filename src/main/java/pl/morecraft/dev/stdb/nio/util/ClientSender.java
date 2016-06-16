package pl.morecraft.dev.stdb.nio.util;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.morecraft.dev.stdb.command.Command;
import pl.morecraft.dev.stdb.logic.registry.ClientChannelRegistry;

import javax.inject.Inject;

@Component
public class ClientSender {

    private final static Logger logger = LoggerFactory.getLogger(ClientSender.class);

    @Inject
    private ClientChannelRegistry clientChannelRegistry;

    public void send(Command command, String message) {
        Channel channel;
        if (command.getSourceChannelId() != null && (channel = clientChannelRegistry.get(command.getSourceChannelId())) != null) {
            logger.info("Sending command output to requester: {}", command);
            channel.writeAndFlush(
                    message
            );
        }
    }

    public void send(String channelId, String message) {
        Channel channel;
        if (channelId != null && (channel = clientChannelRegistry.get(channelId)) != null) {
            logger.info("Sending message output to requester: {}", message.replaceAll("\r", " ").replaceAll("\n", " "));
            channel.writeAndFlush(
                    message
            );
        }
    }

}
