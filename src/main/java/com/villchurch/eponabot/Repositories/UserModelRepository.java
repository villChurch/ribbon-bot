package com.villchurch.eponabot.Repositories;

import com.villchurch.eponabot.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserModelRepository extends JpaRepository<UserModel, Long> {
}
