package com.villchurch.eponabot.Helpers;

import com.villchurch.eponabot.Repositories.BirthdayRepository;
import com.villchurch.eponabot.models.Birthday;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BirthdayHelper {

    @Autowired
    BirthdayRepository getBirthdayRepository;

    private static BirthdayRepository birthdayRepository;

    @PostConstruct
    private void init() {
        birthdayRepository = getBirthdayRepository;
    }

    public static List<Birthday> getTodaysBirthdays() {
        Instant date = Instant.now();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(date));
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        List<Birthday> allBirthdays = birthdayRepository.findAll();
        return allBirthdays.stream().filter(x -> x.getDay() == day && x.getMonth() == month)
                .toList();
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
