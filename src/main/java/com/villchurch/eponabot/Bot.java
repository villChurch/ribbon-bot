package com.villchurch.eponabot;

import com.villchurch.eponabot.Listeners.GuildMemberListener;
import com.villchurch.eponabot.Listeners.InteractionListener;
import com.villchurch.eponabot.Listeners.SlashListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

@Service
public class Bot {

    private static JDA jda;

    @Value("${app.discord.bot.token}")
    private String token;

    public Bot() {

        jda = JDABuilder.createLight(token, EnumSet.noneOf(GatewayIntent.class))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.playing("Watching ribbons"))
                .addEventListeners(EponaBotApplication.eWaiter, new SlashListener(), new InteractionListener())
                .build();
    }

    public static void Setup() {
        // These commands might take a few minutes to be active after creation/update/delete
        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(
                Commands.slash("say", "Makes the bot say what you tell it to")
                        .addOption(STRING, "content", "What the bot should say", true),
                Commands.slash("add_ribbon", "add a new ribbon")
                        .addOption(STRING, "ribbon_image", "URL to image for the ribbon", true)
                        .addOption(STRING, "ribbon_name", "Name of the ribbon", true)
                        .addOption(STRING, "ribbon_description", "Description for the ribbon", false)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS)),
                Commands.slash("delete_ribbon", "delete a ribbon")
                        .addOption(STRING, "ribbon_name", "Name of the ribbon to delete", true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS)),
                Commands.slash("give_ribbon", "give a ribbon to someone")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS))
                        .addOption(OptionType.USER, "user", "user to give ribbon to")
                        .addOption(STRING, "ribbon_name", "name of ribbon to give user", false)
                        .addOption(INTEGER, "ribbon_id", "Id of ribbon to give user", false),
                Commands.slash("show_ribbon", "show a ribbon")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS))
                        .addOption(STRING, "ribbon_name", "name of ribbon to show", true),
                Commands.slash("profile", "show your profile")
                        .addOption(USER, "user", "user to show ribbons for. If blank will show your own"
                        , false),
                Commands.slash("take_ribbon", "take a ribbon from someone")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS))
                        .addOption(STRING, "ribbon_name", "Name of the ribbon", true)
                        .addOption(USER, "user", "user to take ribbon from"),
                Commands.slash("reset", "reset a users ribbons")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS))
                        .addOption(USER, "user", "user to reset ribbons for", true),
                Commands.slash("show_ribbons", "Show all ribbons")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS))
        ).queue();
    }

}
