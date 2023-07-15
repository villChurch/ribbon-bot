package com.villchurch.eponabot.Repositories;

import com.villchurch.eponabot.models.EyeSpyAwards;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EyeSpyAwardsRepository extends JpaRepository<EyeSpyAwards, Long> {

    Optional<EyeSpyAwards> findByPoints(Integer points);

}
