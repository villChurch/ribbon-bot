package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.menu.ButtonEmbedPaginator;
import com.villchurch.eponabot.EponaBotApplication;
import com.villchurch.eponabot.Repositories.RibbonRepository;
import com.villchurch.eponabot.Repositories.UserRibbonsRepository;
import com.villchurch.eponabot.exceptions.RibbonNotFoundException;
import com.villchurch.eponabot.models.Ribbon;
import com.villchurch.eponabot.models.UserRibbons;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Component
public class RibbonCommands {

    @Autowired
    public RibbonRepository getRibbonRepository;
    @Autowired
    public UserRibbonsRepository getUserRibbonsRepository;

    @PostConstruct
    public void init() {
        ribbonRepository = getRibbonRepository;
        userRibbonsRepository = getUserRibbonsRepository;
    }

    public static RibbonRepository ribbonRepository;

    public static UserRibbonsRepository userRibbonsRepository;


    public static void AddRibbon(SlashCommandInteractionEvent event,
                                 String ribbonImage, String ribbonName, String ribbonDescription) {
        event.deferReply(true).queue();
        Ribbon newRibbon = new Ribbon();
        newRibbon.setName(ribbonName);
        newRibbon.setPath(ribbonImage);
        newRibbon.setDescription(Objects.requireNonNullElse(ribbonDescription, "no description"));
        ribbonRepository.save(newRibbon);
        event.getHook().sendMessage("Ribbon created successfully.").queue();
    }

    public static void GiveRibbon(SlashCommandInteractionEvent event, Integer ribbonId) throws RibbonNotFoundException {
        event.reply("Choose the user/users to give the ribbon to")
                .addActionRow(EntitySelectMenu.create("give_ribbon_" + ribbonId.toString(),
                                EntitySelectMenu.SelectTarget.USER)
                        .build())
                .setEphemeral(true)
                .queue();
    }

    public static void GiveRibbon(SlashCommandInteractionEvent event,
                                  User member, String ribbonName) throws RibbonNotFoundException {
        event.deferReply(true).queue();
        Ribbon ribbonToGive = ribbonRepository.findByName(ribbonName).stream()
                .findFirst().orElseThrow(() -> new RibbonNotFoundException(event, ribbonName));
        AssignRibbon(member, ribbonToGive);
        event.getHook().sendMessage(ribbonName + " given to " + member.getName())
                .queue();
    }

    public static void GiveRibbon(SlashCommandInteractionEvent event, User member, Integer id) throws RibbonNotFoundException {
        event.deferReply(true).queue();
        Ribbon ribbonToGive = ribbonRepository.findById(Long.valueOf(id)).stream()
                .findFirst().orElseThrow(() -> new RibbonNotFoundException(event, id.toString()));
        AssignRibbon(member, ribbonToGive);
        event.getHook().sendMessage(ribbonToGive.getName() + " given to " + member.getName())
                .queue();
    }

    public static void AssignRibbon(User member, Integer ribbonId) throws RibbonNotFoundException {
        Ribbon ribbon = ribbonRepository.findById(Long.valueOf(ribbonId)).stream()
                .findFirst().orElseThrow(() -> new RibbonNotFoundException(ribbonId.toString()));
        AssignRibbon(member, ribbon);
    }

    public static void AssignRibbon(User member, Ribbon ribbon) {
        UserRibbons userRibbonMapping = new UserRibbons();
        userRibbonMapping.setUserid(member.getId());
        userRibbonMapping.setRibbonid(ribbon.getId());
        userRibbonsRepository.save(userRibbonMapping);
    }

    public static void TakeRibbon(SlashCommandInteractionEvent event, User member,
                                  String ribbonName) throws RibbonNotFoundException {
        event.deferReply(true).queue();
        Ribbon ribbonToTake = ribbonRepository.findByName(ribbonName).stream()
                .findFirst().orElseThrow(() -> new RibbonNotFoundException(event, ribbonName));
        List<UserRibbons> matchingRibbons = userRibbonsRepository.findByUserid(member.getId())
                .stream()
                .filter(r -> r.getRibbonid() == ribbonToTake.getId())
                .collect(Collectors.toList());
        if (matchingRibbons.size() < 1) {
            event.getHook().sendMessage(member.getName() + " does not have this ribbon.").queue();
            return;
        }
        userRibbonsRepository.deleteAll(matchingRibbons);
        event.getHook().sendMessage("Ribbon removed from " + member.getName()).queue();
    }

    public static void DeleteRibbon(SlashCommandInteractionEvent event, String ribbonName) {
        event.deferReply(true).queue();
        List<Ribbon> foundRibbons = ribbonRepository.findAll().stream()
                .filter(ri -> ri.getName().equals(ribbonName))
                .collect(Collectors.toList());
        try {
            foundRibbons.forEach(ribbon -> ribbonRepository.delete(ribbon));
        } catch (DataIntegrityViolationException ex) {
            event.getHook().sendMessage("Cannot delete this ribbon as it is currently assigned to users.")
                    .queue();
            return;
        }
        event.getHook().sendMessage("Deleted " + (long) foundRibbons.size() + " ribbons called " + ribbonName)
                .queue();
    }

    public static void ShowRibbon(SlashCommandInteractionEvent event, String ribbonName) {
        Optional<Ribbon> ribbon = ribbonRepository.findByName(ribbonName).stream().findFirst();
        if (ribbon.isEmpty()) {
            event.reply("There are no ribbons by this name").setEphemeral(true).queue();
        } else {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(ribbonName);
            eb.setDescription(ribbon.get().getDescription());
            eb.setImage(ribbon.get().getPath());
            event.replyEmbeds(eb.build()).queue();
        }
    }

    public static void ResetUsersRibbons(SlashCommandInteractionEvent event, User user) {
        event.deferReply(true).queue();
        List<UserRibbons> usersRibbons = userRibbonsRepository.findByUserid(user.getId());
        if (usersRibbons.size() < 1) {
            event.getHook().sendMessage("This user does not have any ribbons").queue();
        } else {
            userRibbonsRepository.deleteAll(usersRibbons);
            event.getHook().sendMessage("All ribbons deleted for " + user.getName()).queue();
        }
    }

    public static void ListAllRibbons(SlashCommandInteractionEvent event) {
        List<Ribbon> ribbons = ribbonRepository.findAll();
        Collections.reverse(ribbons);
        List<MessageEmbed> embeds = new ArrayList<>();
        ribbons.forEach(ribbon -> {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle(ribbon.getName())
                    .setDescription(ribbon.getDescription())
                    .setImage(ribbon.getPath())
                    .addField("Id", Long.toString(ribbon.getId()), true)
                    .build();
            embeds.add(embed);
        });
        ButtonEmbedPaginator paginator = new ButtonEmbedPaginator.Builder()
                .setTimeout(1, TimeUnit.MINUTES)
                .waitOnSinglePage(true)
                .setEventWaiter(EponaBotApplication.eWaiter)
                .addItems(embeds)
                .build();
        try {
            event.replyEmbeds(new EmbedBuilder().setDescription("Checking for ribbons...").build())
                    .queue(interactionHook -> {
                        interactionHook.retrieveOriginal()
                                .queue(message -> paginator.paginate(message, 0));
                    });
        } catch (IllegalArgumentException ex) {
            event.reply(ex.getMessage()).queue();
        }
    }
}
