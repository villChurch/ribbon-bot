package com.villchurch.eponabot.Repositories;

import com.villchurch.eponabot.models.Birthday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BirthdayRepository extends JpaRepository<Birthday, Long> {
}
