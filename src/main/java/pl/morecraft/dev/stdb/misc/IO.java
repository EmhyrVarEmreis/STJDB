package pl.morecraft.dev.stdb.misc;

import java.io.*;
import java.util.Base64;

public class IO {

    /**
     * Read the object from Base64 string.
     */
    public static Object fromString(String s) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    /**
     * Write the object to a Base64 string.
     */
    public static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream bAos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bAos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(bAos.toByteArray());
    }

}
