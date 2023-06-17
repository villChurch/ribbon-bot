package com.villchurch.eponabot.Listeners;

import com.villchurch.eponabot.exceptions.RibbonNotFoundException;
import com.villchurch.eponabot.slashcommands.RibbonCommands;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class InteractionListener extends ListenerAdapter {

    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
        if(event.getComponentId().startsWith("give_ribbon_")) {
            String ribbonIdString = event.getComponentId().split("give_ribbon_")[1];
            if (ribbonIdString == null) {
                event.reply("There has been an error parsing the ribbon id").setEphemeral(true)
                        .queue();
                throw new RuntimeException("Error parsing ribbon id");
            } else {
                Integer ribbonId = Integer.parseInt(ribbonIdString);
                List<User> users = event.getMentions().getUsers();
                users.forEach(user -> {
                    try {
                        RibbonCommands.AssignRibbon(user, ribbonId);
                        event.reply("Ribbon with id " + ribbonId + " given to " + user.getName())
                                .setEphemeral(true)
                                .queue();
                    } catch (RibbonNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }
}
