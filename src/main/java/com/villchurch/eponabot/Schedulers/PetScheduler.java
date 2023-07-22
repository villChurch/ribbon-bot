package com.villchurch.eponabot.Schedulers;

import com.villchurch.eponabot.Repositories.UserPetsRepository;
import com.villchurch.eponabot.models.Userpets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PetScheduler {

    @Autowired
    UserPetsRepository userPetsRepository;

    @Scheduled(cron = "0 0 20 1/2 * ? *", zone = "UTC")
    private void AgePets() {
        List<Userpets> childPets = userPetsRepository.findAll().stream()
                .filter(p -> !p.isAdult())
                .collect(Collectors.toList());
        childPets.forEach(cp -> {
            cp.setAdult(true);
            userPetsRepository.save(cp);
        });
    }
}
