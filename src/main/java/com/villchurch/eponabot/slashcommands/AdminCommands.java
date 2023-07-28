package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminCommands extends SlashCommand {
    public AdminCommands() {
        this.name = "admin";
        this.help = "admin commands";
        this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        this.children = new SlashCommand[] {
                new Activity()
        };
    }
    @Override
    protected void execute(SlashCommandEvent event) {

    }

    public static class Activity extends SlashCommand {
        public Activity() {
            this.name = "activity";
            this.help = "Set the activity message for the bot";
            this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "message", "new status message for the bot")
                    .setRequired(true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply(true).queue();
            String msg = Objects.requireNonNull(event.getOption("message")).getAsString();
            event.getJDA().getPresence().setActivity(net.dv8tion.jda.api.entities.Activity.playing(msg));
            event.getHook().sendMessage("Activity changed to - " + msg).queue();
        }
    }
}
