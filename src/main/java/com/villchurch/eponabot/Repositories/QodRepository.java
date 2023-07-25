package com.villchurch.eponabot.Repositories;

import com.villchurch.eponabot.models.Qod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QodRepository extends JpaRepository<Qod, Long> {
}
