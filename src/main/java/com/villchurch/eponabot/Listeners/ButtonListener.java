package com.villchurch.eponabot.Listeners;

import com.villchurch.eponabot.Helpers.PetHelper;
import com.villchurch.eponabot.models.PetsButtons;
import com.villchurch.eponabot.models.Userpets;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;
import java.util.Optional;

public class ButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Optional<PetsButtons> optional = PetHelper.getPetButtonByButtonId(event.getComponentId());
        if (optional.isPresent()) {
            PetsButtons petsButtons = optional.get();
            Userpets pet = new Userpets();
            pet.setOwner(Objects.requireNonNull(event.getMember()).getId());
            pet.setName("New pet!");
            pet.setPetid(petsButtons.getPetid());
            pet.setAdult(false);
            PetHelper.saveUsersPet(pet);
            event.reply("You have succesfully claimed this pet.").setEphemeral(true).queue();
        } else if (event.getComponentId().startsWith("claim")){
            event.reply("Looks like you were too late to claim this pet :(").setEphemeral(true).queue();
        }
    }
}
