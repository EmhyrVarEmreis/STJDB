package pl.morecraft.dev.stdb.logic.registry;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class NodeDistributor {

    private final static Logger logger = LoggerFactory.getLogger(NodeDistributor.class);

    @Inject
    private NodeChannelRegistry channelRegistry;

    private int counter = 0;

    public Channel getNext() {
        Channel channel = null;
        if (channelRegistry.getChannelIDs().size() > 0) {
            channel = channelRegistry.get(
                    channelRegistry.getChannelIDs().get(counter % channelRegistry.getChannelIDs().size())
            );
        }
        logger.info("Retrieving next channel: {}", channel == null ? "null" : channel.id().asShortText());
        counter++;
        return channel;
    }

}
