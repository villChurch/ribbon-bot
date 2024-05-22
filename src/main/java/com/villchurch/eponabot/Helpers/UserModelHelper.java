package com.villchurch.eponabot.Helpers;
import com.villchurch.eponabot.Repositories.UserLinkRepository;
import com.villchurch.eponabot.Repositories.UserModelRepository;
import com.villchurch.eponabot.models.UserLink;
import com.villchurch.eponabot.models.UserModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserModelHelper {

    @Autowired
    UserModelRepository getUserModelRepository;

    @Autowired
    UserLinkRepository getUserLinkRepository;
    private static UserModelRepository userModelRepository;
    private static UserLinkRepository userLinkRepository;

    @PostConstruct
    public void init() {
        userModelRepository = getUserModelRepository;
        userLinkRepository = getUserLinkRepository;
    }

    public static void saveUserModel(UserModel userModel) {
        userModelRepository.save(userModel);
    }

    public static void deleteUserLink(UserLink userLink) {
        userLinkRepository.delete(userLink);
    }

    public static void saveUserLink(UserLink userLink) {
        userLinkRepository.save(userLink);
    }

    public static Optional<UserLink> getByCode(String code) {
        return userLinkRepository.findByCode(code);
    }

    public static UserModel getById(long id) {
        Optional<UserModel> optionalUserModel = userModelRepository.findById(id);
        return optionalUserModel.orElse(null);
    }
}
