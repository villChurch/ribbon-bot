package com.villchurch.eponabot.service;

import com.villchurch.eponabot.Repositories.UserRepository;
import com.villchurch.eponabot.models.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserHelper {


    private static UserRepository userRepository;

    @Autowired
    private UserRepository getUserRepository;

    @PostConstruct
    private void init() {
        userRepository = getUserRepository;
    }



    public static Optional<User> findUserByUserId (String UserId) {
        return userRepository.findByUserid(UserId);
    }

    public static void Save(User user) {
        userRepository.save(user);
    }
}
