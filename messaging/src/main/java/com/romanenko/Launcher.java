package com.romanenko;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration;

@SpringBootApplication(
        exclude = {
                CassandraDataAutoConfiguration.class,
                CassandraReactiveDataAutoConfiguration.class
        }
)
public class Launcher {

    public static void main(String[] args) {
        try {
            SpringApplication.run(Launcher.class, args);
        } catch (Exception ex) {
            System.exit(1);
        }
    }

}
