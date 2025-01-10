package com.villchurch.eponabot.Helpers;

import com.villchurch.eponabot.Repositories.ConfigRepository;
import com.villchurch.eponabot.models.Config;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ConfigHelper {

    @Autowired
    ConfigRepository getConfigRepository;

    private static ConfigRepository configRepository;
    @PostConstruct
    private void init() {
        configRepository = getConfigRepository;
    }

    public static void SaveConfig(Config config) {
        configRepository.save(config);
    }
    public static void DeleteConfig(Config config) {
        configRepository.delete(config);
    }

    public static void DeleteConfig(long id) {
        configRepository.deleteById(id);
    }

    public static Optional<Config> GetConfigById(long id) {
        return configRepository.findById(id);
    }

    public static List<Config> GetConfigValues() {
        return configRepository.findAll();
    }
}
