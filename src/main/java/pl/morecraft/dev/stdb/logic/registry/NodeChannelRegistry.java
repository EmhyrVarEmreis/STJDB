package pl.morecraft.dev.stdb.logic.registry;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "singleton")
public class NodeChannelRegistry extends ChannelRegistry {

}
