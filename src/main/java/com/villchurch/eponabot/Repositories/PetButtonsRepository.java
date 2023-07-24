package com.villchurch.eponabot.Repositories;

import com.villchurch.eponabot.models.PetsButtons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetButtonsRepository extends JpaRepository<PetsButtons, Long> {

    Optional<PetsButtons> findByButton(String button);

    List<PetsButtons> findByPetid(Long petid);
}
