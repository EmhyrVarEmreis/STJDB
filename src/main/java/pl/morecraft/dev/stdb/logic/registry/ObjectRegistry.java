package pl.morecraft.dev.stdb.logic.registry;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope(value = "singleton")
public class ObjectRegistry {

    private Map<String, String> objectChannelMap;

    public ObjectRegistry() {
        this.objectChannelMap = new HashMap<>();
    }

    public synchronized String put(String key, String channelID) {
        return objectChannelMap.put(key, channelID);
    }

    public synchronized String get(String key) {
        return objectChannelMap.get(key);
    }

    public String remove(String key) {
        return objectChannelMap.remove(key);
    }

}
