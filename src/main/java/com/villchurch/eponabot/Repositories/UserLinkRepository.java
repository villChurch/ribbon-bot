package com.villchurch.eponabot.Repositories;

import com.villchurch.eponabot.models.UserLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLinkRepository extends JpaRepository<UserLink, Long> {

    Optional<UserLink> findByCode(String code);

}
