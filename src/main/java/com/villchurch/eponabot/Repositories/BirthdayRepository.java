package com.villchurch.eponabot.Repositories;

import com.villchurch.eponabot.models.Birthday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BirthdayRepository extends JpaRepository<Birthday, Long> {
}
