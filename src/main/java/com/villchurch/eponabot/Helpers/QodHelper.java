package com.villchurch.eponabot.Helpers;

import com.villchurch.eponabot.Repositories.QodRepository;
import com.villchurch.eponabot.models.Qod;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QodHelper {

    @Autowired
    QodRepository getQodRepository;

    private static QodRepository qodRepository;

    @PostConstruct
    private void init() {
        qodRepository = getQodRepository;
    }

    public static void saveQod(Qod qod) {
        qodRepository.save(qod);
    }

    public static void deleteQod(Qod qod) {
        qodRepository.delete(qod);
    }

    public static List<Qod> returnQodList() {
        return qodRepository.findAll();
    }

    public static List<Qod> returnUnpostedQodList() {
        return  returnQodList().stream().filter(q -> !q.isPosted()).collect(Collectors.toList());
    }
}
