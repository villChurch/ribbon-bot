package com.villchurch.eponabot.ContextMenus;

import com.jagrosh.jdautilities.command.UserContextMenu;
import com.jagrosh.jdautilities.command.UserContextMenuEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonEmbedPaginator;
import com.villchurch.eponabot.EponaBotApplication;
import com.villchurch.eponabot.Helpers.RibbonHelper;
import com.villchurch.eponabot.models.Ribbon;
import com.villchurch.eponabot.models.UserRibbons;
import com.villchurch.eponabot.slashcommands.ProfileCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ProfileContextMenu extends UserContextMenu {

    private static final EventWaiter eWaiter = EponaBotApplication.eWaiter;
    public ProfileContextMenu() {
        this.name = "Show Ribbons";
    }

    protected void execute(UserContextMenuEvent event) {
        User user = event.getTarget();
        List<Ribbon> matchedRibbons = returnUsersRibbons(user.getId());
        List<MessageEmbed> ribbons = returnRibbonEmbeds(matchedRibbons, user);
        if (matchedRibbons.size() < 1) {
            event.respond(user.getName() + " currently has no ribbons.");
        } else {
            ButtonEmbedPaginator buttonEmbedPaginator = returnButtonEmbedPagiator(ribbons);
            try {
                event.replyEmbeds(new EmbedBuilder().setDescription("Checking for ribbons...").build())
                        .queue(interactionHook -> interactionHook.retrieveOriginal()
                                .queue(message -> buttonEmbedPaginator.paginate(message, 0)));
            } catch (IllegalArgumentException ex) {
                event.respond(ex.getMessage());
            }
        }
    }

    private static List<Ribbon> returnUsersRibbons(String userId) {
        List<UserRibbons> usersRibbons = RibbonHelper.getUserRibbons(userId);
        List<Long> ribbonIds = usersRibbons.stream()
                .map(UserRibbons::getRibbonid)
                .collect(Collectors.toList());
        List<Ribbon> allRibbons = RibbonHelper.getAllRibbons();
        return allRibbons.stream()
                .filter(ribbon -> ribbonIds.contains(ribbon.getId()))
                .collect(Collectors.toList());
    }

    private static List<MessageEmbed> returnRibbonEmbeds(List<Ribbon> usersRibbons, User user) {
        List<MessageEmbed> ribbons = new ArrayList<>();
        Collections.reverse(usersRibbons);
        usersRibbons.forEach(ribbon -> {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle(ribbon.getName())
                    .setDescription(ribbon.getDescription())
                    .setImage(ribbon.getPath())
                    .setFooter(user.getName() + "'s Ribbons")
                    .build();
            ribbons.add(embed);
        });
        return ribbons;
    }

    private static ButtonEmbedPaginator returnButtonEmbedPagiator(List<MessageEmbed> items) {
        return new ButtonEmbedPaginator.Builder()
                .addItems(items)
                .waitOnSinglePage(true)
                .setTimeout(1, TimeUnit.MINUTES)
                .setEventWaiter(ProfileContextMenu.eWaiter)
                .build();
    }
}
