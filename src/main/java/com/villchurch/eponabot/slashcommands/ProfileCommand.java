package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonEmbedPaginator;
import com.villchurch.eponabot.EponaBotApplication;
import com.villchurch.eponabot.Helpers.RibbonHelper;
import com.villchurch.eponabot.models.Ribbon;
import com.villchurch.eponabot.models.UserRibbons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ProfileCommand extends SlashCommand {

    private static final EventWaiter eWaiter = EponaBotApplication.eWaiter;
    public ProfileCommand() {
        this.name = "profile";
        this.help = "show a profile for a user";
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "user to show profile for")
                .setRequired(false));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        User user = slashCommandEvent.getUser();
        if (slashCommandEvent.hasOption("user")) {
            user = Objects.requireNonNull(slashCommandEvent.getOption("user")).getAsUser();
        }
        List<Ribbon> matchedRibbons = returnUsersRibbons(user.getId());
        List<MessageEmbed> ribbons = returnRibbonEmbeds(matchedRibbons, slashCommandEvent.getUser());
        if (matchedRibbons.size() < 1) {
            slashCommandEvent.reply(user.getName() + " currently has no ribbons").queue();
        } else {
            ButtonEmbedPaginator buttonEmbedPaginator = returnButtonEmbedPagiator(ribbons);
            try {
                slashCommandEvent.replyEmbeds(new EmbedBuilder().setDescription("Checking for ribbons...").build())
                        .queue(interactionHook -> interactionHook.retrieveOriginal()
                                .queue(message -> buttonEmbedPaginator.paginate(message, 0)));
            } catch (IllegalArgumentException ex) {
                slashCommandEvent.reply(ex.getMessage()).queue();
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
        List<UserRibbons> usersRibbons = RibbonHelper.getUserRibbons(userId);
        List<Long> ribbonIds = usersRibbons.stream()
                .map(UserRibbons::getRibbonid)
                .collect(Collectors.toList());
        List<Ribbon> allRibbons = RibbonHelper.getAllRibbons();
        return allRibbons.stream()
                .filter(ribbon -> ribbonIds.contains(ribbon.getId()))
                .collect(Collectors.toList());
    }
    private static ButtonEmbedPaginator returnButtonEmbedPagiator(List<MessageEmbed> items) {
        return new ButtonEmbedPaginator.Builder()
                .addItems(items)
                .waitOnSinglePage(true)
                .setTimeout(1, TimeUnit.MINUTES)
                .setEventWaiter(ProfileCommand.eWaiter)
                .build();
    }
}
