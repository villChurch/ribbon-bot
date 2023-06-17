package com.villchurch.eponabot.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SayCommand {


    public static void Say(SlashCommandInteractionEvent event, String content) {
        event.reply(content).queue();
    }
}
