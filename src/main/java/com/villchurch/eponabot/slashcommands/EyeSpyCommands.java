package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.villchurch.eponabot.Helpers.EyeSpyHelper;
import com.villchurch.eponabot.Helpers.RibbonHelper;
import com.villchurch.eponabot.models.EyeSpy;
import com.villchurch.eponabot.models.EyeSpyAwards;
import com.villchurch.eponabot.models.Ribbon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EyeSpyCommands extends SlashCommand {

    public EyeSpyCommands() {
        this.name = "eye_spy";
        this.help= "eye spy commands";
        this.children = new SlashCommand[] {new Leaderboard(), new Verify(), new Profile(), new RewardAdd(), new RewardRemove() };
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
                    .setEphemeral(false)
                    .queue();
        }
    }
    private static class Verify extends SlashCommand {

        public Verify() {
            this.name = "award";
            this.help = "Award a person a point";
            this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.USER, "user", "user to give point to")
                    .setRequired(true));
            this.options = options;
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply().queue();
            User user = Objects.requireNonNull(
                    slashCommandEvent.getOption("user")).getAsUser();

            EyeSpy eyeSpy = EyeSpyHelper.returnUsersEyeSpy(user.getId());
            eyeSpy.setPoints(eyeSpy.getPoints() + 1);
            int newPoints = eyeSpy.getPoints();
            EyeSpyHelper.saveEyeSpy(eyeSpy);
            Optional<EyeSpyAwards> optionalAward = EyeSpyHelper.findAwardByPoints(newPoints);
            if (optionalAward.isPresent()) {
                Optional<Ribbon> ribbon = RibbonHelper.getRibbonById((long) optionalAward.get().getRibbonid());
                if (ribbon.isEmpty()) {
                    slashCommandEvent.getHook().sendMessage("1 point added to " + user.getName())
                            .queue();
                } else {
                    RibbonHelper.AssignRibbon(user, ribbon.get());
                    MessageEmbed embed = new EmbedBuilder()
                            .setTitle("New Award")
                            .setDescription("1 point added to " + user.getName()
                                    + ". " + user.getName() + " has now reached " + newPoints + " points and has been awarded " +
                                    "the following ribbon " + ribbon.get().getName())
                            .setImage(ribbon.get().getPath())
                            .build();
                    slashCommandEvent.getHook().sendMessageEmbeds(embed).queue();
                }
            } else {
                slashCommandEvent.getHook().sendMessage("1 point added to " + user.getName())
                        .queue();
            }
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

    private static class RewardAdd extends SlashCommand {
        public RewardAdd() {
            this.name = "reward_add";
            this.help = "add a reward tier to eyespy";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER,
                    "points", "number of points needed for this reward").setRequired(true));
            options.add(new OptionData(OptionType.INTEGER,
                    "ribbon_id", "ID of ribbon to give").setRequired(true));
            this.options = options;
            this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        }

        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply().setEphemeral(true).queue();
            int points = Objects.requireNonNull(slashCommandEvent.getOption("points")).getAsInt();
            int ribbonId = Objects.requireNonNull(slashCommandEvent.getOption("ribbon_id")).getAsInt();
            if (RibbonHelper.getRibbonById((long) ribbonId).isPresent()) {
                EyeSpyAwards award = new EyeSpyAwards();
                award.setPoints(points);
                award.setRibbonid(ribbonId);
                EyeSpyHelper.saveEyeSpyAward(award);
                slashCommandEvent.getHook().sendMessage("New award created at " + points + "points.")
                        .queue();
            } else {
                slashCommandEvent.getHook().sendMessage("No ribbon found with id " + ribbonId)
                        .queue();
            }
        }
    }
    private static class RewardRemove extends SlashCommand {
        public RewardRemove() {
            this.name = "reward_remove";
            this.help = "remove an award";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER,
                    "points", "points level you want to remove").setRequired(true));
            this.options = options;
            this.userPermissions =  new Permission[] { Permission.ADMINISTRATOR };
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply().setEphemeral(true).queue();
            int points = Objects.requireNonNull(slashCommandEvent.getOption("points")).getAsInt();
            Optional<EyeSpyAwards> optionalAward = EyeSpyHelper.findAwardByPoints(points);
            if (optionalAward.isPresent()) {
                EyeSpyHelper.deleteEyeSpyAward(optionalAward.get());
                slashCommandEvent.getHook().sendMessage("Reward removed at " + points + " points.")
                        .queue();
            } else {
                slashCommandEvent.getHook().sendMessage("No reward found at " + points + " points.")
                        .queue();
            }
        }
    }
}
