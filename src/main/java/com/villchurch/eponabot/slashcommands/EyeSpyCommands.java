package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.villchurch.eponabot.Helpers.EyeSpyHelper;
import com.villchurch.eponabot.models.EyeSpy;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EyeSpyCommands extends SlashCommand {

    public EyeSpyCommands() {
        this.name = "eye_spy";
        this.help= "eye spy commands";
        this.children = new SlashCommand[] {new Leaderboard(), new Verify(), new Profile()};
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {

    }

    private static class Profile extends SlashCommand {
        public Profile() {
            this.name = "profile";
            this.help = "Show your points/profile";
        }

        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            EyeSpy eyeSpy = EyeSpyHelper.returnUsersEyeSpy(slashCommandEvent.getUser().getId());
            slashCommandEvent.reply("You currently have " + eyeSpy.getPoints() + " points")
                    .setEphemeral(true)
                    .queue();
        }
    }
    private static class Verify extends SlashCommand {

        public Verify() {
            this.name = "award";
            this.help = "Award a person a point";
            this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.USER, "user", "user to give point to"));
            this.options = options;
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            User user = Objects.requireNonNull(
                    slashCommandEvent.getOption("user")).getAsUser();

            EyeSpy eyeSpy = EyeSpyHelper.returnUsersEyeSpy(user.getId());
            eyeSpy.setPoints(eyeSpy.getPoints() + 1);
            EyeSpyHelper.saveEyeSpy(eyeSpy);
            slashCommandEvent.reply("1 point added to " + user.getName())
                    .setEphemeral(true)
                    .queue();
        }
    }

    private static class Leaderboard extends SlashCommand {

        public Leaderboard() {
            this.name = "leaderboard";
            this.help = "Display leaderboard";
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            List<EyeSpy> leaderboard = EyeSpyHelper.getLeaderboard();
            if (leaderboard.isEmpty()) {
                slashCommandEvent.reply("There is no one on the leaderboard yet.")
                        .setEphemeral(true)
                        .queue();
            } else {
                StringBuilder msg = new StringBuilder();
                msg.append("Current leaderboard \n");
                for (int i = 1; i <= leaderboard.size(); i++) {
                    var es = leaderboard.get(i-1);
                    msg.append(i)
                            .append(". <@")
                            .append(es.getUserid())
                            .append("> - ")
                            .append(es.getPoints())
                            .append("\n");
                }
                slashCommandEvent.reply(msg.toString()).queue();
            }
        }
    }
}
