package pl.morecraft.dev.stdb.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Component
@Scope(value = "singleton")
public class SharedCommandQueue {

    private final static Logger logger = LoggerFactory.getLogger(SharedCommandQueue.class);

    private volatile Queue<Command> queue;

    public SharedCommandQueue() {
        queue = new LinkedList<>();
    }

    public synchronized boolean add(Command command) {
        logger.info("Adding command to queue: {}", command);
        return queue.add(command);
    }

    public synchronized Command pool() {
        Command command = queue.poll();
        if (command != null) { //Do not print empty commands
            logger.info("Retrieving command from queue: {}", command);
        } else {
            logger.debug("Retrieving command from queue: null");
        }
        return command;
    }

}
