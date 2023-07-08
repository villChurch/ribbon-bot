package com.villchurch.eponabot.Listeners;

import com.villchurch.eponabot.exceptions.RibbonNotFoundException;
import com.villchurch.eponabot.slashcommands.ProfileCommand;
import com.villchurch.eponabot.slashcommands.RibbonCommands;
import com.villchurch.eponabot.slashcommands.SayCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class SlashListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // Only accept commands from guilds
        if (event.getGuild() == null)
            return;
        switch (event.getName())
        {
            case "say":
                SayCommand.Say(event, Objects.requireNonNull(event.getOption("content")).getAsString()); // content is required so no null-check here
                break;
            case "add_ribbon":
                if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.KICK_MEMBERS)) {
                    RibbonCommands.AddRibbon(event,
                            Objects.requireNonNull(event.getOption("ribbon_image")).getAsString(),
                            Objects.requireNonNull(event.getOption("ribbon_name")).getAsString(),
                            Objects.requireNonNull(event.getOption("ribbon_description")).getAsString());
                } else {
                    event.reply("You do not have the required permissions to run this command")
                            .setEphemeral(true).queue();
                }
                break;
            case "delete_ribbon":
                if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.KICK_MEMBERS)) {
                    RibbonCommands.DeleteRibbon(event,
                            Objects.requireNonNull(event.getOption("ribbon_name")).getAsString());
                } else {
                    event.reply("You do not have the required permissions to run this command")
                            .setEphemeral(true).queue();
                }
                break;
            case "show_ribbon":
                if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.KICK_MEMBERS)) {
                    RibbonCommands.ShowRibbon(event,
                            Objects.requireNonNull(event.getOption("ribbon_name")).getAsString());
                } else {
                    event.reply("You do not have the required permissions to run this command")
                            .setEphemeral(true).queue();
                }
                break;
            case "give_ribbon":
                if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.KICK_MEMBERS)) {
                    if (event.getOption("ribbon_id") != null && event.getOptions().size() > 1) {
                        try {
                            RibbonCommands.GiveRibbon(event, Objects.requireNonNull(event.getOption("user")).getAsUser(),
                                    Objects.requireNonNull(event.getOption("ribbon_id")).getAsInt());
                        } catch (RibbonNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    } else if((long) event.getOptions().size() == 1) {
                        try {
                            RibbonCommands.GiveRibbon(event,
                                    Objects.requireNonNull(event.getOption("ribbon_id")).getAsInt());
                        } catch (RibbonNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            RibbonCommands.GiveRibbon(event, Objects.requireNonNull(event.getOption("user")).getAsUser(),
                                    Objects.requireNonNull(event.getOption("ribbon_name")).getAsString());
                        } catch (RibbonNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    event.reply("You do not have the required permissions to run this command")
                            .setEphemeral(true).queue();
                }
                break;
            case "take_ribbon":
                if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.KICK_MEMBERS)) {
                    try {
                        RibbonCommands.TakeRibbon(event, Objects.requireNonNull(event.getOption("user")).getAsUser(),
                                Objects.requireNonNull(event.getOption("ribbon_name")).getAsString());
                    } catch (RibbonNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    event.reply("You do not have the required permissions to run this command")
                            .setEphemeral(true).queue();
                }
                break;
            case "reset":
                if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.KICK_MEMBERS)) {
                    RibbonCommands.ResetUsersRibbons(event, Objects.requireNonNull(event.getOption("user")).getAsUser());
                } else {
                    event.reply("You do not have the required permissions to run this command")
                            .setEphemeral(true).queue();
                }
                break;
            case "show_ribbons":
                if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.KICK_MEMBERS)) {
                    RibbonCommands.ListAllRibbons(event);
                } else {
                    event.reply("You do not have the required permissions to run this command.")
                            .setEphemeral(true).queue();
                }
                break;
            case "profile":
                if (event.getOptions().size() > 0) {
                    ProfileCommand.showUsersProfile(event,
                            Objects.requireNonNull(event.getOption("user")).getAsUser());
                } else {
                    ProfileCommand.execute(event);
                }
                break;
            default:
//                event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
                break;
        }
    }

}
