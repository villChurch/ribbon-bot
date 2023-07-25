package com.villchurch.eponabot.Schedulers;

import com.villchurch.eponabot.Helpers.QodHelper;
import com.villchurch.eponabot.models.Qod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QodScheduler {

    String qodWebhook;

    public QodScheduler(@Value("${app.discord.live.qod.webhook}")
                        String getQodWebhook) {
        this.qodWebhook = getQodWebhook;
    }

    @Scheduled(cron = "0 0 20 * * ?", zone = "UTC")
    public void PostQuestion() {
        if (QodHelper.returnQodList().isEmpty()) {
            return;
        }
        String data = "";
        List<Qod> unpostedQuestions = QodHelper.returnUnpostedQodList();
        if (unpostedQuestions.isEmpty()) {
            //reset status
            List<Qod> qods = QodHelper.returnQodList();
            qods.forEach(q -> {
                q.setPosted(false);
                QodHelper.saveQod(q);
            });
            unpostedQuestions = qods;
        }
        int randomQNumber = new Random().nextInt(unpostedQuestions.size());
        Qod randomQ = unpostedQuestions.get(randomQNumber);
        randomQ.setPosted(true);
        QodHelper.saveQod(randomQ);
        data = randomQ.getQuestion();
        log.info("Question is {}", data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Radishbooty_qod");
        RestTemplate restTemplate = new RestTemplate();
        String requestJson = "{\n" +
                "  \"username\":\"Radishbooty\",\n" +
                "  \"avatar_url\":\"https://cdn.discordapp.com/attachments/745012634889355264/1133485616571617370/radishbooty_2.png\",\n" +
                "  \"content\":\"" + data +"\"" +
                "\n}";
        log.info(requestJson);
        log.info("data =========> {}", data);
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        restTemplate.postForEntity(qodWebhook, entity, String.class);
    }
}
