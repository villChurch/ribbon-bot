package com.villchurch.eponabot.Repositories;

import com.villchurch.eponabot.models.UserRibbons;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRibbonsRepository extends JpaRepository<UserRibbons, Long> {

    List<UserRibbons> findByUserid(String userid);
}
