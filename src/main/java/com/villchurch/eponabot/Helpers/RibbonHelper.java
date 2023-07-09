package com.villchurch.eponabot.Helpers;

import com.villchurch.eponabot.Repositories.RibbonRepository;
import com.villchurch.eponabot.Repositories.UserRibbonsRepository;
import com.villchurch.eponabot.models.Ribbon;
import com.villchurch.eponabot.models.UserRibbons;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RibbonHelper {

    @Autowired
    RibbonRepository getRibbonRepository;

    @Autowired
    UserRibbonsRepository getUrr;

    private static RibbonRepository ribbonRepository;

    private static UserRibbonsRepository userRibbonsRepository;
    @PostConstruct
    public void init() {
        ribbonRepository = getRibbonRepository;
        userRibbonsRepository = getUrr;
    }

    public static void saveRibbon(Ribbon ribbon) {
        ribbonRepository.save(ribbon);
    }

    public static Optional<Ribbon> getRibbonById(Long id) {
        return ribbonRepository.findById(id);
    }

    public static void AssignRibbon(User member, Ribbon ribbon) {
        UserRibbons userRibbonMapping = new UserRibbons();
        userRibbonMapping.setUserid(member.getId());
        userRibbonMapping.setRibbonid(ribbon.getId());
        userRibbonsRepository.save(userRibbonMapping);
    }

    public static List<UserRibbons> getUserRibbons(String userId) {
        return userRibbonsRepository.findByUserid(userId);
    }

    public static void deleteAllMatchingRibbons(List<UserRibbons> matchedRibbons) {
        userRibbonsRepository.deleteAll(matchedRibbons);
    }

    public static List<Ribbon> getAllRibbons() {
        return ribbonRepository.findAll();
    }

    public static void deleteRibbon(Ribbon ribbon) {
        ribbonRepository.delete(ribbon);
    }

    public static List<Ribbon> getRibbonByName(String name) {
        return ribbonRepository.findByName(name);
    }
}
