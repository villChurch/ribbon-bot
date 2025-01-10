package com.villchurch.eponabot.Schedulers;

import com.villchurch.eponabot.Helpers.BirthdayHelper;
import com.villchurch.eponabot.Repositories.BirthdayRepository;
import com.villchurch.eponabot.Repositories.ConfigRepository;
import com.villchurch.eponabot.models.Birthday;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class BirthdayScheduler {


    String birthdayWebhook;

    public BirthdayScheduler(@Value("${app.discord.live.birthday.webhook}") String getBirthdayWebhook) {
        this.birthdayWebhook = getBirthdayWebhook;
    }
    @Autowired
    BirthdayRepository birthdayRepository;

    @Autowired
    ConfigRepository configRepository;

    @Scheduled(cron = "0 0 13 * * ?", zone = "UTC")
    public void PostBirthdays() {
        Instant date = Instant.now();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(date));
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        List<Birthday> allBirthdays = birthdayRepository.findAll();
        List<Birthday> todaysBirthdays =
                allBirthdays.stream().filter(x -> x.getDay() == day && x.getMonth() == month).toList();
        if(!todaysBirthdays.isEmpty()){
            todaysBirthdays.forEach(b -> SendBirthday(b.getUser()));
        }
    }

    private void SendBirthday(String user) {
        String usr = "<@" + user + ">";
        String msg = "It's " + usr + "'s birthday today! Go " + usr + ", it's your birthday!";
        SendWebhook("Radishbooty_Birthday", "Radishbooty", "https://cdn.discordapp.com/attachments/745012634889355264/1133485616571617370/radishbooty_2.png",
                msg, birthdayWebhook);
    }

    private void SendWebhook(String userAgent, String username, String avatarUrl, String data, String webHook) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", userAgent);
        RestTemplate restTemplate = new RestTemplate();
        String requestJson = "{\n" +
                "  \"username\":\"" + username + "\",\n" +
                "  \"avatar_url\":\""+ avatarUrl + "\",\n" +
                "  \"content\":\"" + data +"\"" +
                "\n}";
        log.info(requestJson);
        log.info("data =========> {}", data);
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        restTemplate.postForEntity(webHook, entity, String.class);
    }
}
