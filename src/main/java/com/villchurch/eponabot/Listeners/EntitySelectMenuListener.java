package com.villchurch.eponabot.Listeners;

import com.villchurch.eponabot.Helpers.RibbonHelper;
import com.villchurch.eponabot.models.Ribbon;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.Optional;

public class EntitySelectMenuListener extends ListenerAdapter {

    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
        if(event.getComponentId().startsWith("give-ribbon-")) {
            event.deferReply().setEphemeral(true).queue();
            String ribbonIdString = event.getComponentId().split("give-ribbon-")[1];
            long ribbonId = Long.parseLong(ribbonIdString);
            Optional<Ribbon> ribbonOptional = RibbonHelper.getRibbonById(ribbonId);
            Ribbon ribbon;
            if (ribbonOptional.isEmpty()) {
                event.getHook().sendMessage("No ribbon found for id " + ribbonId).queue();
            } else {
                ribbon = ribbonOptional.get();
                List<User> users = event.getMentions().getUsers();
                users.forEach(user -> {
                    RibbonHelper.AssignRibbon(user, ribbon);
                });
                event.getHook().sendMessage("Ribbon assigned to " + users.size() + " users.").queue();
            }
        }
    }
}
