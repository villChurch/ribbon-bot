package com.villchurch.eponabot.Helpers;

import com.villchurch.eponabot.Repositories.PetButtonsRepository;
import com.villchurch.eponabot.Repositories.PetsRepository;
import com.villchurch.eponabot.Repositories.UserPetsRepository;
import com.villchurch.eponabot.models.Pets;
import com.villchurch.eponabot.models.PetsButtons;
import com.villchurch.eponabot.models.Userpets;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PetHelper {

    @Autowired
    PetsRepository getPetsRepository;

    @Autowired
    UserPetsRepository getUserPetsRepository;

    @Autowired
    PetButtonsRepository getPetButtonsRepository;

    public static long randomPetId = 19;

    private static PetsRepository petsRepository;

    private static UserPetsRepository userPetsRepository;

    private static PetButtonsRepository petButtonsRepository;

    @PostConstruct
    private void init() {
        petsRepository = getPetsRepository;
        userPetsRepository = getUserPetsRepository;
        petButtonsRepository = getPetButtonsRepository;
    }

    public static List<Userpets> findByUserpetsByPetId(Long petid) {
        return userPetsRepository.findByPetid(petid);
    }

    public static Optional<PetsButtons> getPetButtonByButtonId(String buttonId) {
        return petButtonsRepository.findByButton(buttonId);
    }

    public static void savePetButton(PetsButtons petsButtons) {
        petButtonsRepository.save(petsButtons);
    }

    public static List<PetsButtons> findPetButtonsByPetId(Long petId) {
        return petButtonsRepository.findByPetid(petId);
    }

    public static void deletePetsButtons(PetsButtons petsButtons) {
        petButtonsRepository.delete(petsButtons);
    }

    public static void deletePetsButtons(List<PetsButtons> petsButtonsList) {
        petButtonsRepository.deleteAll(petsButtonsList);
    }

    public static Optional<Pets> getPetById(Long id) {
        return petsRepository.findAll().stream().filter(p -> p.getId() == id).findFirst();
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

    public static void deletePet(Pets pets) {
        petsRepository.delete(pets);
    }
}
