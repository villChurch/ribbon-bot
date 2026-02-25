package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.menu.ButtonEmbedPaginator;
import com.villchurch.eponabot.EponaBotApplication;
import com.villchurch.eponabot.Helpers.BirthdayHelper;
import com.villchurch.eponabot.models.Birthday;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Slf4j
public class BirthdayCommands extends SlashCommand {

    public BirthdayCommands() {
        this.name = "birthday";
        this.help = "Birthday commands";
        this.children = new SlashCommand[] {
                new AddBirthday(),
                new GetBirthdays(),
                new DeleteBirthday()
        };
    }
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {

    }

    private static class DeleteBirthday extends SlashCommand {

        public DeleteBirthday() {
            this.name = "delete";
            this.help = "delete a birthday";
            this.userPermissions = new Permission[]{
                    Permission.ADMINISTRATOR
            };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER, "id", "Id of birthday to delete").setRequired(true));
            this.options = options;
        }
        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply().setEphemeral(true).queue();
            int id = Objects.requireNonNull(event.getOption("id")).getAsInt();
            List<Birthday> birthdays = BirthdayHelper.getAllBirthdays();
            Predicate<Birthday> predicate = b -> b.getId() == id;
            Optional<Birthday> birthdayOptional = birthdays.stream().filter(predicate).findFirst();
            birthdayOptional.ifPresent(BirthdayHelper::deleteBirthday);
            event.getHook().sendMessage("Birthday with id - " + id + " has been deleted if it existed.").queue();
        }
    }

    private static class GetBirthdays extends SlashCommand {
        public GetBirthdays() {
            this.name = "list";
            this.help = "lists all birthdays";
            this.userPermissions = new Permission[] {
                    Permission.ADMINISTRATOR
            };
        }
        @Override
        protected void execute(SlashCommandEvent event) {
            List<Birthday> birthdays = BirthdayHelper.getAllBirthdays();
            if (birthdays.isEmpty()) {
                event.reply("There are no Birthdays set currently.").setEphemeral(true).queue();
            } else  {
                ButtonEmbedPaginator buttonEmbedPaginator = returnButtonEmbedPaginator(returnBirthdayEmbed(birthdays));
                try {
                    event.replyEmbeds(new EmbedBuilder().setDescription("Checking for birthdays...").build())
                            .queue(interactionHook -> interactionHook.retrieveOriginal()
                                    .queue(msg -> buttonEmbedPaginator.paginate(msg, 0)));
                } catch (IllegalArgumentException ex) {
                    event.reply(ex.getMessage()).setEphemeral(true).queue();
                }
            }
        }
    }

    private static class AddBirthday extends SlashCommand {

        public AddBirthday() {
            this.name = "add";
            this.help = "add birthday";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER, "month", "month of your birthday").setRequired(true));
            options.add(new OptionData(OptionType.INTEGER, "day", "day of your birthday").setRequired(true));
            this.options = options;
        }
        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply().setEphemeral(false).queue();
            int month = Objects.requireNonNull(event.getOption("month")).getAsInt();
            int day = Objects.requireNonNull(event.getOption("day")).getAsInt();
            boolean valid = true;
            if (month > 12 || month < 1) {
                event.getHook().sendMessage("Month must be a value between 1 and 12").queue();
                valid = false;
            }
            if (day > 31 || day < 1) {
                event.getHook().sendMessage("Day must be a value between 1 and 31").queue();
                valid = false;
            }
            if (valid) {
                Birthday birthday = new Birthday();
                birthday.setMonth(month);
                birthday.setDay(day);
                birthday.setUser(event.getUser().getId());
                try {
                    BirthdayHelper.saveBirthday(birthday);
                    event.getHook().sendMessage("Your birthday has been added successfully").queue();
                }
                catch (Exception e) {
                    log.error(e.getMessage());
                    event.getHook().sendMessage("There was an error adding your birthday").queue();
                }
            }
        }
    }

    private static List<MessageEmbed> returnBirthdayEmbed(List<Birthday> birthdays) {
        List<MessageEmbed> embeds = new ArrayList<>();
        birthdays.forEach(birthday-> {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("ID - " + birthday.getId())
                    .addField(new MessageEmbed.Field("User", birthday.getUser(), false))
                    .addField(new MessageEmbed.Field("Day", birthday.getDay().toString(), false))
                    .addField(new MessageEmbed.Field("Month", birthday.getMonth().toString(), false))
                    .build();
            embeds.add(embed);
        });
        return embeds;
    }

    private static ButtonEmbedPaginator returnButtonEmbedPaginator(List<MessageEmbed> items) {
        return new ButtonEmbedPaginator.Builder()
                .addItems(items)
                .waitOnSinglePage(true)
                .setTimeout(1, TimeUnit.MINUTES)
                .setEventWaiter(EponaBotApplication.eWaiter)
                .build();
    }
}
