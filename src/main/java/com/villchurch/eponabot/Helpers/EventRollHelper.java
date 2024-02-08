package com.villchurch.eponabot.Helpers;

import com.villchurch.eponabot.Repositories.EventRollRepository;
import com.villchurch.eponabot.models.EventRoll;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventRollHelper {

    @Autowired
    EventRollRepository getEventRollRepository;

    private static EventRollRepository eventRollRepository;

    @PostConstruct
    private void init() {
        eventRollRepository = getEventRollRepository;
    }


    public static List<EventRoll> ReturnAllEvents() {
        return eventRollRepository.findAll();
    }

    public static void SaveEvent(EventRoll eventRoll) {
        eventRollRepository.save(eventRoll);
    }

    public static void DeleteEvent(long eventId) {
        eventRollRepository.deleteById(eventId);
    }

    public static List<EventRoll> ReturnEventsForType(String eventType) {
        return eventRollRepository.findByEventtype(eventType);
    }
}
