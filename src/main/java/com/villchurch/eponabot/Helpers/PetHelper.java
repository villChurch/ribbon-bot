package com.villchurch.eponabot.Helpers;

import com.villchurch.eponabot.Repositories.PetsRepository;
import com.villchurch.eponabot.Repositories.UserPetsRepository;
import com.villchurch.eponabot.models.Pets;
import com.villchurch.eponabot.models.Userpets;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PetHelper {

    @Autowired
    PetsRepository getPetsRepository;

    @Autowired
    UserPetsRepository getUserPetsRepository;

    private static PetsRepository petsRepository;

    private static UserPetsRepository userPetsRepository;

    @PostConstruct
    private void init() {
        petsRepository = getPetsRepository;
        userPetsRepository = getUserPetsRepository;
    }

    public static Optional<Userpets> getUsersPetById(Long id) {
        return userPetsRepository.findById(id);
    }

    public static List<Pets> returnAllPets() {
        return petsRepository.findAll();
    }

    public static List<Userpets> getUsersPetByUserId(String userId) {
        return userPetsRepository.findByOwner(userId);
    }

    public static void saveUsersPet(Userpets userpets) {
        userPetsRepository.save(userpets);
    }

    public static Pets savePets(Pets pets) {
        return petsRepository.save(pets);
    }

    public static String returnAdultLinkFromPetId(Long petId) {
        Optional<Pets> optional = petsRepository.findById(petId);
        if (optional.isEmpty()) {
            return "";
        } else {
            return optional.get().getAdultlink();
        }
    }

    public static String returnChildLinkFromPetId(Long petId) {
        Optional<Pets> optional = petsRepository.findById(petId);
        if (optional.isEmpty()) {
            return "";
        } else {
            return optional.get().getChildlink();
        }
    }

    public static void removePetFromUser(Userpets userpets) {
        userPetsRepository.delete(userpets);
    }
}
