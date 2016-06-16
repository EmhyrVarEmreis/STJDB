package pl.morecraft.dev.stdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import pl.morecraft.dev.stdb.run.NodeRunner;

@SpringBootApplication
@EnableConfigurationProperties
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
})
public class NodeApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(NodeApplication.class, args);

        NodeRunner bean = context.getBean(NodeRunner.class);
        bean.run();

    }

}
