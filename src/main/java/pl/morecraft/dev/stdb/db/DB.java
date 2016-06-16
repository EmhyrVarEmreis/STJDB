package pl.morecraft.dev.stdb.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.morecraft.dev.stdb.command.Command;
import pl.morecraft.dev.stdb.command.InvalidCommandException;
import pl.morecraft.dev.stdb.db.exception.RecordOutOfRangeException;
import pl.morecraft.dev.stdb.db.proto.BasicDB;

import java.util.*;

@Service
public class DB implements BasicDB {

    private final static Logger logger = LoggerFactory.getLogger(DB.class);

    private Map<String, Record> recordMap;

    public DB() {
        this.recordMap = new HashMap<>();
    }

    @Override
    public Record insert(Record record) throws RecordOutOfRangeException {
        logger.info("Received task to INSERT record: {}", record);
        recordMap.put(record.getKey(), record);
        return record;
    }

    @Override
    public Record delete(String key) throws RecordOutOfRangeException {
        logger.info("Received task to DELETE record with key: {}", key);
        return recordMap.remove(key);
    }

    @Override
    public Record update(Record record) throws RecordOutOfRangeException {
        logger.info("Received task to UPDATE record: {}", record);
        return insert(record);
    }

    @Override
    public Record select(String key) throws RecordOutOfRangeException {
        logger.info("Received task to SELECT record with key: {}", key);
        return recordMap.get(key);
    }

    @Override
    public List<Record> select() {
        logger.info("Received task to SELECT ALL records");
        return new ArrayList<>(recordMap.values());
    }

    @Override
    public List<Record> execute(Command command) throws InvalidCommandException, NullPointerException {
        logger.info("Trying to execute command: {}", command);
        List<Record> result = new LinkedList<>();
        switch (command.getCommandType()) {
            case INSERT:
                for (Record record : command.getData()) {
                    result.add(insert(record));
                }
                break;
            case DELETE:
                for (String key : command.getParams()) {
                    result.add(delete(key));
                }
                break;
            case UPDATE:
                for (Record record : command.getData()) {
                    result.add(update(record));
                }
                break;
            case SELECT:
                for (String key : command.getParams()) {
                    result.add(select(key));
                }
                break;
            case WELCOME:
            case EXCEPTION:
            case ACK:
            case QUIT:
                logger.info("Non-db command: {}", command);
                break;
            default:
                throw new InvalidCommandException("Invalid command: " + command);
        }
        return result;
    }

}
