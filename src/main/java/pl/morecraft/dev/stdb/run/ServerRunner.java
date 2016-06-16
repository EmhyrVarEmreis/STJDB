package pl.morecraft.dev.stdb.run;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.morecraft.dev.stdb.logic.CommandExecutor;
import pl.morecraft.dev.stdb.nio.Receiver;
import pl.morecraft.dev.stdb.nio.Server;

import javax.inject.Inject;

@Service
public final class ServerRunner implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(ServerRunner.class);

    @Inject
    private Server server;

    @Inject
    private Receiver receiver;

    @Inject
    private CommandExecutor commandExecutor;

    @Override
    public void run() {
        Thread serverThread = new Thread(
                () -> {
                    try {
                        server.run();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        //e.printStackTrace();
                    }
                }
        );
        serverThread.setName("SERVER");

        Thread receiverThread = new Thread(
                () -> {
                    try {
                        receiver.run();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        //e.printStackTrace();
                    }
                }
        );
        receiverThread.setName("RECEIVER");

        Thread commandExecutorThread = new Thread(
                () -> {
                    try {
                        commandExecutor.run();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        //e.printStackTrace();
                    }
                }
        );
        commandExecutorThread.setName("C-EXECUTOR");

        serverThread.start();
        receiverThread.start();
        commandExecutorThread.start();
    }

}
