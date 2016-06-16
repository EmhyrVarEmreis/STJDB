package pl.morecraft.dev.stdb.db.proto;

import pl.morecraft.dev.stdb.command.Command;
import pl.morecraft.dev.stdb.db.Record;

import java.util.List;

public interface BasicDB {

    Record insert(Record record);

    Record delete(String key);

    Record update(Record record);

    Record select(String key);

    List<Record> select();

    List<Record> execute(Command command);

}
