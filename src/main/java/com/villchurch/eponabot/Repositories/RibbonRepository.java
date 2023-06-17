package com.villchurch.eponabot.Repositories;

import com.villchurch.eponabot.models.Ribbon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RibbonRepository extends JpaRepository<Ribbon, Long> {

    List<Ribbon> findByName(String name);

}
