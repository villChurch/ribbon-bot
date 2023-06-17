package com.villchurch.eponabot.Listeners;

import com.villchurch.eponabot.models.User;
import com.villchurch.eponabot.service.UserHelper;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class GuildMemberListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        log.info("User update triggered by user joining guild {}", event.getGuild().getName());
        updateMember(event.getMember());
    }

    @Override
    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        log.info("Guild {} ready. Updating {} users.", guild.getName(),
                guild.getMembers().size());
        List<Member> memberList = guild.getMembers();
        memberList.forEach(this::updateMember);
    }

    @Override
    public void onGuildMemberUpdateNickname(@Nonnull GuildMemberUpdateNicknameEvent event) {
        log.info("User update triggered by nickname change.");
        updateMember(event.getMember());
    }

    private void updateMember(Member member) {
        String id = member.getId();
        Optional<User> optionalUser = UserHelper.findUserByUserId(id);
        User user;
        if (optionalUser.isEmpty()) {
            log.info("UserID {} not found in database so creating new user.", id);
            user = new User();
            user.setUserid(member.getId());
            user.setEffectiveName(member.getEffectiveName());
        } else {
            log.info("UserID {} found in database so updating user.", id);
            user = optionalUser.get();
            user.setEffectiveName(member.getEffectiveName());
        }
        UserHelper.Save(user);
    }


}
