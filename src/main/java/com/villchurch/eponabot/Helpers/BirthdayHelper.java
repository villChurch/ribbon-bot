package com.villchurch.eponabot.Helpers;

import com.villchurch.eponabot.Repositories.BirthdayRepository;
import com.villchurch.eponabot.models.Birthday;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class BirthdayHelper {

    @Autowired
    BirthdayRepository getBirthdayRepository;

    private static BirthdayRepository birthdayRepository;

    @PostConstruct
    private void init() {
        birthdayRepository = getBirthdayRepository;
    }

    public static void saveBirthday(Birthday birthday) {
        birthdayRepository.save(birthday);
    }

    public static void deleteBirthday(Birthday birthday) {
        birthdayRepository.delete(birthday);
    }

    public static List<Birthday> getAllBirthdays() {
        return birthdayRepository.findAll();
    }
}
