package pl.morecraft.dev.stdb.logic.registry;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class ChannelRegistry {

    private final static Logger logger = LoggerFactory.getLogger(ChannelRegistry.class);

    private Channel serverChannel;
    private Map<String, Channel> channelMap;
    private List<String> channelIDs;

    public ChannelRegistry() {
        this.channelMap = new HashMap<>();
        this.channelIDs = new LinkedList<>();
    }

    public Channel getServerChannel() {
        return serverChannel;
    }

    public void setServerChannel(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }

    public Channel put(Channel channel) {
        channelIDs.add(channel.id().asShortText());
        return channelMap.put(channel.id().asShortText(), channel);
    }

    public Channel remove(Channel channel) {
        return remove(channel.id().asShortText());
    }

    public Channel remove(String channelId) {
        return channelMap.remove(channelId);
    }

    public Channel get(String channelId) {
        return channelMap.get(channelId);
    }

    public List<String> getChannelIDs() {
        return channelIDs;
    }

    public void refresh() {
        Iterator<Map.Entry<String, Channel>> i = channelMap.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, Channel> entry = i.next();
            if (entry.getValue() == null || !entry.getValue().isActive() || !entry.getValue().isOpen()) {
                logger.info("Removing closed node from register: {}", entry.getValue() == null ? "null" : entry.getValue());
                i.remove();
            }
        }
    }
}
