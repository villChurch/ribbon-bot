package com.villchurch.eponabot.exceptions;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class RibbonNotFoundException extends Exception{

    public RibbonNotFoundException(SlashCommandInteractionEvent event, String ribbonName) {
        super("Could not find ribbon called " + ribbonName);
    }

    public RibbonNotFoundException(String ribbonId) {
        super("Could not find ribbon with id " + ribbonId);
    }
}
