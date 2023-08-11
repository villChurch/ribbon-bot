package com.villchurch.eponabot.Repositories;

import com.villchurch.eponabot.models.Movies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movies, Long> {
}
