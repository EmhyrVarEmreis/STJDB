package pl.morecraft.dev.stdb.run;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.morecraft.dev.stdb.nio.Node;

import javax.inject.Inject;

@Service
public final class NodeRunner implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(NodeRunner.class);

    @Inject
    private Node node;

    @Override
    public void run() {
        Thread thread = new Thread(
                () -> {
                    try {
                        node.run();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
        );
        thread.setName("NODE");
        thread.start();
    }

}
