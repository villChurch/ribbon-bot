package com.villchurch.eponabot.slashcommands;

import com.villchurch.eponabot.Repositories.StickyRepository;
import com.villchurch.eponabot.models.Sticky;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class StickyCommand {

    @Autowired
    StickyRepository getStickyRepository;

    @PostConstruct
    public void init(){
        stickyRepository = getStickyRepository;
    }

    private static  StickyRepository stickyRepository;

    public static void CreateSticky(SlashCommandInteractionEvent event, String message) {
        Message msg = event.getChannel().sendMessage(message).complete();
        Sticky sticky = new Sticky();
        sticky.setMessage(message);
        sticky.setChannelid(event.getChannel().getId());
        sticky.setGuildid(Objects.requireNonNull(event.getGuild()).getId());
        sticky.setMessageid(msg.getId());
        stickyRepository.save(sticky);
        event.reply("Sticky created").setEphemeral(true).queue();
    }

    public static void DeleteSticky(SlashCommandInteractionEvent event, String messageId) {
        stickyRepository.deleteByMessageid(messageId);
        event.getChannel().deleteMessageById(messageId).queue();
        event.reply("Sticky removed").setEphemeral(true).queue();
    }
}
