package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.villchurch.eponabot.Helpers.UserModelHelper;
import com.villchurch.eponabot.models.UserLink;
import com.villchurch.eponabot.models.UserModel;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class WebsiteCommands extends SlashCommand {

    public WebsiteCommands() {
        this.name = "website";
        this.help = "website commands";
//        this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        this.children = new SlashCommand[] {
            new DiscordLinkCommand()
        };
    }
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {

    }

    public static class DiscordLinkCommand extends SlashCommand {

        public DiscordLinkCommand() {
            this.name = "link";
            this.help = "link your discord account to your website account";
        }

        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply().queue();
            String code = getCaptcha();
            UserLink userLink = new UserLink();
            userLink.setDiscordid(slashCommandEvent.getUser().getId());
            userLink.setCode(code);
            UserModelHelper.saveUserLink(userLink);
            slashCommandEvent.getHook().sendMessage("Your code is: " + code).queue();
        }
        private String getCaptcha() {
            char[] data = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                    'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                    'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
                    'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                    'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6',
                    '7', '8', '9'};
            char[] index = new char[7];

            Random r = new Random();
            int i = 0;
            for (i = 0; i < (index.length); i++) {
                int ran = r.nextInt(data.length);
                index[i] = data[ran];
            }
            return new String(index);
        }
    }

}
