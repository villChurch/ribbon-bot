package com.villchurch.eponabot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.villchurch.eponabot.slashcommands.EyeSpyCommands;
import com.villchurch.eponabot.slashcommands.ProfileCommand;
import com.villchurch.eponabot.slashcommands.RibbonCommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Component
public class Bot {

    private static JDA jda;
    private String token;
    public Bot( @Value("${app.discord.bot.live.token}")
                String getToken) {
        this.token = getToken;
        CommandClientBuilder client = new CommandClientBuilder();
        client.addSlashCommands(GetSlashCommands());
        client.setOwnerId("272151652344266762");
        client.setActivity(Activity.playing("Watching ribbons"));
//        client.forceGuildOnly("798239862477815819"); // for testing
        CommandClient commandClient = client.build();

        jda = JDABuilder.createLight(token, EnumSet.noneOf(GatewayIntent.class))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.playing("Booting....."))
                .addEventListeners(EponaBotApplication.eWaiter, commandClient)
                .build();

        jda.getGuilds().forEach(guild -> guild.updateCommands().queue());
        jda.updateCommands().queue();
    }

    private SlashCommand[] GetSlashCommands() {
        List<SlashCommand> commands = new ArrayList<>();
        commands.add(new EyeSpyCommands());
        commands.add(new RibbonCommands());
        commands.add(new ProfileCommand());
        return commands.toArray(new SlashCommand[0]);
    }

    public static void Setup() {
        // These commands might take a few minutes to be active after creation/update/delete
//        CommandListUpdateAction commands = jda.updateCommands();
//        commands.addCommands(
//                Commands.slash("profile", "show your profile")
//                        .addOption(USER, "user", "user to show ribbons for. If blank will show your own"
//                        , false)
//        ).queue();
    }

}
