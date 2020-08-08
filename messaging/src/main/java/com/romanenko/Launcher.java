package com.romanenko;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Launcher {

    public static void main(String[] args) {
        try {
            SpringApplication.run(Launcher.class, args);
        } catch (Exception ex) {
            System.exit(1);
        }
    }

}
