package pl.morecraft.dev.stdb.command;

import java.io.Serializable;

public enum CommandType implements Serializable {

    INSERT, DELETE, UPDATE, SELECT, // CRUD
    WELCOME, QUIT, ACK, EXCEPTION, HELP, EXIT

}
