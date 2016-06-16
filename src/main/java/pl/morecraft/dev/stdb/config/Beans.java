package pl.morecraft.dev.stdb.config;

import com.google.gson.JsonParser;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.morecraft.dev.stdb.config.core.Config;

import javax.inject.Inject;
import java.nio.charset.Charset;

@Component
public class Beans {

    @Inject
    private Config config;

    @Bean(name = "charset")
    @Scope(value = "singleton")
    public Charset getCharset() {
        return Charset.forName("UTF-8");
    }

    @Bean(name = "jsonParser")
    @Scope(value = "singleton")
    public JsonParser getJsonParser() {
        return new JsonParser();
    }

    @Bean(name = "defaultDateTimeFormatter")
    @Scope(value = "singleton")
    public DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormat.forPattern(config.getDateFormat());
    }

    @Bean(name = "greeting")
    public String getGreeting() {
        return "Welcome to\r\nSimple Telnet DB\r\n\r\n";
    }

}
