package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonEmbedPaginator;
import com.villchurch.eponabot.EponaBotApplication;
import com.villchurch.eponabot.Repositories.RibbonRepository;
import com.villchurch.eponabot.Repositories.UserRibbonsRepository;
import com.villchurch.eponabot.models.Ribbon;
import com.villchurch.eponabot.models.UserRibbons;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ProfileCommand {

    public static UserRibbonsRepository userRibbonsRepository;
    public static RibbonRepository ribbonRepository;
    @Autowired
    public RibbonRepository getRibbonRepository;
    @Autowired
    public UserRibbonsRepository getUserRibbonsRepository;

    @PostConstruct
    public void init() {
        ribbonRepository = getRibbonRepository;
        userRibbonsRepository = getUserRibbonsRepository;
    }
    private static final EventWaiter eWaiter = EponaBotApplication.eWaiter;


    public static void showUsersProfile(SlashCommandInteractionEvent event, User user) {
        List<Ribbon> matchedRibbons = returnUsersRibbons(user.getId());
        List<MessageEmbed> ribbons = returnRibbonEmbeds(matchedRibbons, user);
        if (matchedRibbons.size() < 1) {
            event.reply(user.getName() + " currently has no ribbons").queue();
        } else {
            ButtonEmbedPaginator buttonEmbedPaginator = returnButtonEmbedPagiator(ribbons, 1, eWaiter);
            try {
                event.replyEmbeds(new EmbedBuilder().setDescription("Checking for ribbons...").build())
                        .queue(interactionHook -> {
                            interactionHook.retrieveOriginal()
                                    .queue(message -> buttonEmbedPaginator.paginate(message, 0));
                        });
            } catch (IllegalArgumentException ex) {
                event.reply(ex.getMessage()).queue();
            }
        }
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
    private static List<Ribbon> returnUsersRibbons(String userId) {
        List<UserRibbons> usersRibbons = userRibbonsRepository.findByUserid(userId);
        List<Long> ribbonIds = usersRibbons.stream()
                .map(UserRibbons::getRibbonid)
                .collect(Collectors.toList());
        List<Ribbon> allRibbons = ribbonRepository.findAll();
        return allRibbons.stream()
                .filter(ribbon -> ribbonIds.contains(ribbon.getId()))
                .collect(Collectors.toList());
    }
    private static ButtonEmbedPaginator returnButtonEmbedPagiator(List<MessageEmbed> items, int timeout, EventWaiter waiter) {
        return new ButtonEmbedPaginator.Builder()
                .addItems(items)
                .waitOnSinglePage(true)
                .setTimeout(timeout, TimeUnit.MINUTES)
                .setEventWaiter(waiter)
                .build();
    }
    public static void execute(SlashCommandInteractionEvent event) {
        List<Ribbon> matchedRibbons = returnUsersRibbons(event.getMember().getUser().getId());
        List<MessageEmbed> ribbons = returnRibbonEmbeds(matchedRibbons, event.getUser());
        if (matchedRibbons.size() < 1) {
            event.reply("You currently have no ribbons").queue();
        } else {
            ButtonEmbedPaginator buttonEmbedPaginator = returnButtonEmbedPagiator(ribbons, 1, eWaiter);
            try {
                event.replyEmbeds(new EmbedBuilder().setDescription("Checking for ribbons...").build())
                                .queue(interactionHook -> {
                                    interactionHook.retrieveOriginal()
                                            .queue(message -> buttonEmbedPaginator.paginate(message, 0));
                                });
            } catch (IllegalArgumentException ex) {
                event.reply(ex.getMessage()).queue();
            }
        }
    }
}
