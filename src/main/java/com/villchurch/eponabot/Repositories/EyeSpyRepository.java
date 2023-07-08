package com.villchurch.eponabot.Repositories;

import com.villchurch.eponabot.models.EyeSpy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EyeSpyRepository extends JpaRepository<EyeSpy, Long> {

    @Query(value="select * from eponaRibbon.eyespy order by points DESC LIMIT 10", nativeQuery = true)
    List<EyeSpy> getLeaderboard();

    Optional<EyeSpy> findByUserid(String userid);
}
