package com.villchurch.eponabot.Helpers;

import com.villchurch.eponabot.Repositories.EyeSpyRepository;
import com.villchurch.eponabot.models.EyeSpy;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EyeSpyHelper {

    @Autowired
    EyeSpyRepository getEyeSpyRepository;

    private static EyeSpyRepository eyeSpyRepository;

    @PostConstruct
    private void init() {
        eyeSpyRepository = getEyeSpyRepository;
    }

    public static List<EyeSpy> getLeaderboard() {
        return eyeSpyRepository.getLeaderboard();
    }

    public static List<EyeSpy> getEyeSpys() {
        return eyeSpyRepository.findAll();
    }

    public static EyeSpy returnUsersEyeSpy(String userid) {
        Optional<EyeSpy> eyeSpyOptional = eyeSpyRepository.findByUserid(userid);
        if (eyeSpyOptional.isPresent()) {
            return  eyeSpyOptional.get();
        } else {
            EyeSpy eyeSpy = new EyeSpy();
            eyeSpy.setUserid(userid);
            eyeSpy.setPoints(0);
            return eyeSpy;
        }
    }

    public static void saveEyeSpy(EyeSpy eyeSpy) {
        eyeSpyRepository.save(eyeSpy);
    }
}
