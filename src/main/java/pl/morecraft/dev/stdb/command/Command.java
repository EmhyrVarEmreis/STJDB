package pl.morecraft.dev.stdb.command;

import pl.morecraft.dev.stdb.db.Record;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Command implements Serializable {

    private final static long serialVersionUID = 1;

    private CommandType commandType;
    private String[] params = {};
    private String sourceChannelId;
    private String nodeChannelId;
    private Record[] data;

    public Command(CommandType commandType, String[] params) {
        this(commandType, params, null, null, null);
    }

    public Command(CommandType commandType, String[] params, String sourceChannelId, String nodeChannelId, Record[] data) {
        this.commandType = commandType;
        this.params = params;
        this.sourceChannelId = sourceChannelId;
        this.nodeChannelId = nodeChannelId;
        this.data = data;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public int getParamsLength() {
        return this.params.length;
    }

    public String getSourceChannelId() {
        return sourceChannelId;
    }

    public void setSourceChannelId(String sourceChannelId) {
        this.sourceChannelId = sourceChannelId;
    }

    public String getNodeChannelId() {
        return nodeChannelId;
    }

    public void setNodeChannelId(String nodeChannelId) {
        this.nodeChannelId = nodeChannelId;
    }

    public Record[] getData() {
        return data;
    }

    public void setData(Record[] data) {
        this.data = data;
    }

    public static Command valueOf(String s, String sourceChannelId) throws InvalidCommandException {
        Command command = valueOf(s);
        command.setSourceChannelId(sourceChannelId);
        return command;
    }

    public static Command valueOf(String s) throws InvalidCommandException {
        String[] x = splitCommand(s);

        if (x.length < 1) {
            throw new InvalidCommandException("Empty command");
        }

        CommandType commandType;

        try {
            commandType = CommandType.valueOf(x[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandException("Invalid command type [" + x[0] + "]", e);
        }

        return new Command(
                commandType,
                x.length > 1 ? Arrays.copyOfRange(x, 1, x.length) : new String[0]
        );
    }

    @Override
    public String toString() {
        return "Command{" +
                "commandType=" + commandType +
                ", params=" + Arrays.toString(params) +
                ", sourceChannelId='" + sourceChannelId + '\'' +
                ", nodeChannelId='" + nodeChannelId + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }

    private static String[] splitCommand(String s) {
        boolean isQuoteOpened = false;
        List<String> l = new LinkedList<>();
        String x = "";
        for (char c : s.toCharArray()) {
            if (c == '"') {
                isQuoteOpened = !isQuoteOpened;
            } else if (c == ' ' && !isQuoteOpened) {
                l.add(x);
                x = "";
                continue;
            }
            x += c;
        }
        l.add(x);
        return l.toArray(new String[l.size()]);
    }

}
