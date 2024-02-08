package com.villchurch.eponabot.Repositories;

import com.villchurch.eponabot.models.EventRoll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRollRepository extends JpaRepository<EventRoll, Long> {

    List<EventRoll> findByEventtype(String eventtype);
}
