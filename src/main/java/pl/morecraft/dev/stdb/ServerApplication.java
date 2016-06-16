package pl.morecraft.dev.stdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import pl.morecraft.dev.stdb.run.ServerRunner;

@SpringBootApplication
@EnableConfigurationProperties
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
})
public class ServerApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);

        ServerRunner bean = context.getBean(ServerRunner.class);
        bean.run();

    }

}
