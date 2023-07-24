package com.villchurch.eponabot.Repositories;

import com.villchurch.eponabot.models.Userpets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPetsRepository extends JpaRepository<Userpets, Long> {

    Optional<Userpets> findById(Long id);

    List<Userpets> findByOwner(String owner);

    List<Userpets> findByPetid(long petid);
}
