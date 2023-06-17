package com.villchurch.eponabot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class EponaBotApplication {

    public static EventWaiter eWaiter = new EventWaiter();
    public static void main (String[] args) {
        SpringApplication.run(EponaBotApplication.class, args);
        Bot.Setup();
    }
}
