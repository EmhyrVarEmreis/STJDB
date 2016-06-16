package pl.morecraft.dev.stdb.db.exception;

public class RecordOutOfRangeException extends RuntimeException {

    public RecordOutOfRangeException(String message) {
        super(message);
    }

    public RecordOutOfRangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordOutOfRangeException(Throwable cause) {
        super(cause);
    }

}
