package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.menu.ButtonEmbedPaginator;
import com.villchurch.eponabot.EponaBotApplication;
import com.villchurch.eponabot.Helpers.RibbonHelper;
import com.villchurch.eponabot.exceptions.RibbonNotFoundException;
import com.villchurch.eponabot.models.Ribbon;
import com.villchurch.eponabot.models.UserRibbons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RibbonCommands extends SlashCommand {

    public RibbonCommands() {
        this.name = "ribbons";
        this.help = "ribbon commands";
        this.children = new SlashCommand[] {
                new AddRibbon(),
                new GiveRibbon(),
                new TakeRibbon(),
                new DeleteRibbon(),
                new ShowRibbon(),
                new AllRibbons(),
                new ResetUser(),
                new GiveRibbonBulk()
        };
    }

    public static class GiveRibbonBulk extends SlashCommand {
        public GiveRibbonBulk() {
            this.name = "give_bulk";
            this.help = "give multiple ribbons to a user";
            this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.USER, "user", "user to give the ribbon to")
                    .setRequired(true));
            options.add(new OptionData(OptionType.STRING, "ids", "comma separated list of ids to give")
                    .setRequired(true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply(true).queue();
            User user = Objects.requireNonNull(slashCommandEvent.getOption("user")).getAsUser();
            String idsString = Objects.requireNonNull(slashCommandEvent.getOption("ids")).getAsString();
            List<Integer> convertedIds = Stream.of(idsString.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            List<Integer> missingIds = new ArrayList<>();
            List<Integer> givenIds = new ArrayList<>();
            convertedIds.forEach(id -> {
                Optional<Ribbon> optional = RibbonHelper.getRibbonById((long) id);
                if (optional.isEmpty()) {
                    missingIds.add(id);
                } else  {
                    givenIds.add(id);
                    Ribbon ribbonToGive = optional.get();
                    RibbonHelper.AssignRibbon(user, ribbonToGive);
                }
            });
            StringBuilder sb = new StringBuilder();
            if (!missingIds.isEmpty()) {
                sb.append("Could not find ribbons for the following ids - ");
                missingIds.forEach(id -> sb.append(id).append(" "));
                sb.append("/n");
            }
            if (!givenIds.isEmpty()) {
                sb.append("Added the following ribbon ids to ").append(user.getName()).append(" - ");
                givenIds.forEach(id -> sb.append(id).append(" "));
            }
            slashCommandEvent.getHook().sendMessage(sb.toString()).queue();
        }
    }
    public static class GiveRibbon extends SlashCommand {

        public GiveRibbon() {
            this.name = "give";
            this.help = "give ribbon to a user";
            this.userPermissions = new Permission[]{ Permission.KICK_MEMBERS };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.USER, "user", "user to give the ribbon to")
                    .setRequired(true));
            options.add(new OptionData(OptionType.INTEGER, "id", "id of the ribbon to give")
                    .setRequired(true));
            this.options = options;
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply(true).queue();
            var member = Objects.requireNonNull(slashCommandEvent.getOption("user")).getAsUser();
            int ribbonId = Objects.requireNonNull(slashCommandEvent.getOption("id")).getAsInt();
            Ribbon ribbonToGive;
            try {
                ribbonToGive = RibbonHelper.getRibbonById((long) ribbonId).stream()
                        .findFirst().orElseThrow(() ->
                                new RibbonNotFoundException(slashCommandEvent, Integer.toString(ribbonId)));
                RibbonHelper.AssignRibbon(member, ribbonToGive);
                slashCommandEvent.getHook().sendMessage(ribbonToGive.getName() + " given to " + member.getName())
                        .queue();
            } catch (RibbonNotFoundException e) {
                slashCommandEvent.getHook().sendMessage("Could not find a ribbon with id " + ribbonId)
                        .queue();
                throw new RuntimeException(e);
            }
        }
    }
    public static class AddRibbon extends SlashCommand {

        public AddRibbon() {
            this.name = "add";
            this.help = "Add a new ribbon";
            this.userPermissions = new Permission[]{ Permission.KICK_MEMBERS };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "name of the ribbon")
                    .setRequired(true));
            options.add(new OptionData(OptionType.STRING, "image_link", "link to ribbon image")
                    .setRequired(true));
            options.add(new OptionData(OptionType.STRING, "description", "ribbon description")
                    .setRequired(false));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply(true).queue();
            Ribbon newRibbon = new Ribbon();
            newRibbon.setName(Objects.requireNonNull(slashCommandEvent.getOption("name"))
                    .getAsString());
            newRibbon.setPath(Objects.requireNonNull(slashCommandEvent.getOption("image_link"))
                    .getAsString());
            newRibbon.setDescription(Objects.requireNonNullElse(
                    Objects.requireNonNull(slashCommandEvent
                            .getOption("description")).getAsString(), "no description"));
            RibbonHelper.saveRibbon(newRibbon);
            slashCommandEvent.getHook().sendMessage("Ribbon created successfully.").queue();
        }
    }

    public static class TakeRibbon extends SlashCommand {
        public TakeRibbon() {
            this.name = "remove";
            this.help = "remove a ribbon from someone";
            this.userPermissions = new Permission[]{ Permission.KICK_MEMBERS };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER, "ribbon_id", "id of ribbon to take away")
                    .setRequired(true));
            options.add(new OptionData(OptionType.USER, "user", "user to take ribbon from")
                    .setRequired(true));
            this.options = options;
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply(true).queue();
            var member = Objects.requireNonNull(slashCommandEvent.getOption("user")).getAsUser();
            Ribbon ribbonToTake;
            try {
                ribbonToTake = RibbonHelper.getRibbonById(
                        (long) Objects.requireNonNull(slashCommandEvent.getOption("ribbon_id")).getAsInt())
                        .stream()
                        .findFirst().orElseThrow(() -> new RibbonNotFoundException(slashCommandEvent,
                                Objects.requireNonNull(slashCommandEvent.getOption("ribbon_id")).getAsString()));
                Ribbon finalRibbonToTake = ribbonToTake;
                List<UserRibbons> matchingRibbons = RibbonHelper.getUserRibbons(member.getId())
                        .stream()
                        .filter(r -> r.getRibbonid() == finalRibbonToTake.getId())
                        .collect(Collectors.toList());
                if (matchingRibbons.size() < 1) {
                    slashCommandEvent.getHook().sendMessage(member.getName() + " does not have this ribbon.")
                            .queue();
                    return;
                }
                RibbonHelper.deleteAllMatchingRibbons(matchingRibbons);
                slashCommandEvent.getHook().sendMessage("Ribbon removed from " + member.getName()).queue();
            } catch (RibbonNotFoundException e) {
                slashCommandEvent.getHook().sendMessage("Error removing ribbon")
                        .setEphemeral(true)
                        .queue();
                throw new RuntimeException(e);
            }

        }
    }

    public static class DeleteRibbon extends SlashCommand {
        public DeleteRibbon() {
            this.name = "delete";
            this.help = "delete a ribbon";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER, "ribbon_id", "id of ribbon to delete")
                    .setRequired(true));
            this.options = options;
            this.userPermissions = new Permission[] { Permission.KICK_MEMBERS };
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply(true).queue();
            List<Ribbon> foundRibbons = RibbonHelper.getAllRibbons()
                    .stream()
                    .filter(ri -> ri.getId() == (long) Objects.requireNonNull(
                            slashCommandEvent.getOption("ribbon_id")).getAsInt())
                    .collect(Collectors.toList());
            try {
                foundRibbons.forEach(RibbonHelper::deleteRibbon);
            } catch (DataIntegrityViolationException ex) {
                slashCommandEvent.getHook().sendMessage(
                        "Cannot delete this ribbon as it is currently assigned to users.")
                        .queue();
                return;
            }
            slashCommandEvent.getHook().sendMessage(
                    "Deleted " + (long) foundRibbons.size() + " ribbons called " +
                            foundRibbons.get(0).getName())
                    .queue();
        }
    }

    public static class ShowRibbon extends SlashCommand {
        public ShowRibbon() {
            this.name = "show";
            this.help = "show a ribbon";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "name of ribbon to show")
                    .setRequired(true));
            this.options = options;
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            Optional<Ribbon> ribbon = RibbonHelper.getRibbonByName(
                    Objects.requireNonNull(slashCommandEvent.getOption("name")).getAsString())
                    .stream()
                    .findFirst();
            if (ribbon.isEmpty()) {
                slashCommandEvent.reply("There are no ribbons by this name").setEphemeral(true).queue();
            } else {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(Objects.requireNonNull(slashCommandEvent.getOption("name")).getAsString());
                eb.setDescription(ribbon.get().getDescription());
                eb.setImage(ribbon.get().getPath());
                slashCommandEvent.replyEmbeds(eb.build()).queue();
            }
        }
    }

    public static class AllRibbons extends SlashCommand {
        public AllRibbons() {
            this.name = "all";
            this.help = "Show all ribbons";
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            List<Ribbon> ribbons = RibbonHelper.getAllRibbons();
            Collections.reverse(ribbons);
            List<MessageEmbed> embeds = new ArrayList<>();
            ribbons.forEach(ribbon -> {
                MessageEmbed embed = new EmbedBuilder()
                        .setTitle(ribbon.getName())
                        .setDescription(ribbon.getDescription())
                        .setImage(ribbon.getPath())
                        .addField("Id", Long.toString(ribbon.getId()), true)
                        .build();
                embeds.add(embed);
            });
            ButtonEmbedPaginator paginator = new ButtonEmbedPaginator.Builder()
                    .setTimeout(1, TimeUnit.MINUTES)
                    .waitOnSinglePage(true)
                    .setEventWaiter(EponaBotApplication.eWaiter)
                    .addItems(embeds)
                    .build();
            try {
                slashCommandEvent.replyEmbeds(new EmbedBuilder().setDescription("Checking for ribbons...").build())
                        .queue(interactionHook -> interactionHook.retrieveOriginal()
                                .queue(message -> paginator.paginate(message, 0)));
            } catch (IllegalArgumentException ex) {
                slashCommandEvent.reply(ex.getMessage()).queue();
            }
        }
    }

    public static class ResetUser extends SlashCommand {
        public ResetUser() {
            this.name = "reset";
            this.help = "reset a users ribbons";
            this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.USER, "user", "user to reset ribbons for")
                    .setRequired(true));
            this.options = options;
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply(true).queue();
            List<UserRibbons> userRibbons = RibbonHelper.getUserRibbons(
                    Objects.requireNonNull(slashCommandEvent.getOption("user")).getAsUser().getId());
            if (userRibbons.size() < 1) {
                slashCommandEvent.getHook().sendMessage("This user does not have any ribbons").queue();
            } else {
                RibbonHelper.deleteAllMatchingRibbons(userRibbons);
                slashCommandEvent.getHook().sendMessage("All ribbons deleted for " +
                        Objects.requireNonNull(slashCommandEvent.getOption("user"))
                                .getAsUser().getName())
                        .queue();
            }
        }
    }
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {

    }
}
