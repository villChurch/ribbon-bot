package com.villchurch.eponabot.Schedulers;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BirthdayScheduler {

    @Scheduled(cron = "0 0 13 * * ?", zone = "UTC")
    public void PostBirthdays() {

    }
}
