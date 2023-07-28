package com.villchurch.eponabot.ContextMenus;

import com.jagrosh.jdautilities.command.UserContextMenu;
import com.jagrosh.jdautilities.command.UserContextMenuEvent;
import com.jagrosh.jdautilities.menu.ButtonEmbedPaginator;
import com.villchurch.eponabot.EponaBotApplication;
import com.villchurch.eponabot.Helpers.PetHelper;
import com.villchurch.eponabot.models.Pets;
import com.villchurch.eponabot.models.Userpets;
import com.villchurch.eponabot.slashcommands.PetCommands;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PetContextMenu extends UserContextMenu {

    public PetContextMenu() {
        this.name = "Show Pets";
    }

    protected void execute(UserContextMenuEvent event) {
        List<Userpets> pets = PetHelper.getUsersPetByUserId(event.getTarget().getId());
        if (pets.isEmpty()) {
            event.reply(event.getTarget().getName() + " currently does not own any pets.").queue();
        } else {
            List<MessageEmbed> petsEmbed = returnPetsEmbed(pets, event.getTarget());
            ButtonEmbedPaginator buttonEmbedPaginator = returnButtonEmbedPaginator(petsEmbed);
            try {
                event.replyEmbeds(new EmbedBuilder().setDescription("Checking for pets...").build())
                        .queue(interactionHook -> interactionHook.retrieveOriginal()
                                .queue(message -> buttonEmbedPaginator.paginate(message, 0)));
            } catch (IllegalArgumentException ex) {
                event.reply(ex.getMessage()).queue();
            }
        }
    }

    private static ButtonEmbedPaginator returnButtonEmbedPaginator(List<MessageEmbed> items) {
        return new ButtonEmbedPaginator.Builder()
                .addItems(items)
                .waitOnSinglePage(true)
                .setTimeout(1, TimeUnit.MINUTES)
                .setEventWaiter(EponaBotApplication.eWaiter)
                .build();
    }

    private static List<MessageEmbed> returnPetsEmbed(List<Userpets> userpets, User user) {
        List<MessageEmbed> pets = new ArrayList<>();
        Collections.reverse(userpets);
        userpets.forEach(p -> {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle(p.getName())
                    .setDescription("ID - " + p.getId())
                    .setImage(p.isAdult() ? PetHelper.returnAdultLinkFromPetId(p.getPetid())
                            : PetHelper.returnChildLinkFromPetId(p.getPetid()))
                    .setFooter(user.getName() + "'s Pets")
                    .build();
            pets.add(embed);
        });
        return pets;
    }
}
