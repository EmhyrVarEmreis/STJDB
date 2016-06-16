package pl.morecraft.dev.stdb.db;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;

public class Record implements Serializable {

    private String key;
    private String value;
    private DateTime timestamp = DateTime.now();

    public Record() {

    }

    public Record(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Record(String key, String value, DateTime timestamp) {
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public String getString() {
        return value;
    }

    public String getValue() {
        return value;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Record{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public String toPrintString(DateTimeFormatter dateTimeFormatter) {
        return "KEY: " + key + "\r\nVALUE: " + value + "\r\nLAST CHANGE DATE: " + timestamp.toString(dateTimeFormatter);
    }

}
