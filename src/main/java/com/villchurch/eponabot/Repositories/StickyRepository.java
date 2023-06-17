package com.villchurch.eponabot.Repositories;

import com.villchurch.eponabot.models.Sticky;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StickyRepository extends JpaRepository<Sticky, Long> {

    public void deleteByMessageid(String messageid);
}
