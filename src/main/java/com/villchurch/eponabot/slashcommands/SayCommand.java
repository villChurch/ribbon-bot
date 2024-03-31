package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SayCommand  extends SlashCommand {
    public SayCommand() {
        this.name = "say";
        this.help = "Say something as ribbon bot";
        this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "message", "What you want the bot to say").setRequired(true));
        options.add(new OptionData(OptionType.CHANNEL, "channel", "Channel you want to send the message in.").setRequired(true));
        this.options = options;
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply().setEphemeral(true).queue();
        var channel = Objects.requireNonNull(event.getOption("channel")).getAsChannel().asTextChannel();
        channel.sendMessage(Objects.requireNonNull(event.getOption("message")).getAsString()).queue();
        event.getHook().sendMessage("Message sent.").queue();
    }
}
