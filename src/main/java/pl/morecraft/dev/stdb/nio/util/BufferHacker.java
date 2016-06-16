package pl.morecraft.dev.stdb.nio.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "singleton")
public class BufferHacker {

    private String data;

    public BufferHacker() {
        this("");
    }

    public BufferHacker(String data) {
        this.data = data;
    }

    public String append(String s) {
        return (data += s);
    }

    public void clear() {
        this.data = "";
    }

    public int length() {
        return data.length();
    }

    public String getData() {
        return this.data;
    }

}
