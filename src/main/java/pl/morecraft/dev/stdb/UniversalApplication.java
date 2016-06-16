package pl.morecraft.dev.stdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import pl.morecraft.dev.stdb.config.core.Config;
import pl.morecraft.dev.stdb.run.NodeRunner;
import pl.morecraft.dev.stdb.run.ServerRunner;

@SpringBootApplication
@EnableConfigurationProperties
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
})
public class UniversalApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(UniversalApplication.class, args);

        Runnable runnable;
        Config config = context.getBean(Config.class);

        if (config.isNode()) {
            runnable = context.getBean(NodeRunner.class);
        } else {
            runnable = context.getBean(ServerRunner.class);
        }

        runnable.run();
    }

}
