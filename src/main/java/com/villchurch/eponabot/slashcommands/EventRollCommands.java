package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonEmbedPaginator;
import com.villchurch.eponabot.EponaBotApplication;
import com.villchurch.eponabot.Helpers.EventRollHelper;
import com.villchurch.eponabot.models.EventRoll;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class EventRollCommands extends SlashCommand {

    private static final EventWaiter eWaiter = EponaBotApplication.eWaiter;

    public EventRollCommands() {
        this.name = "event_roll";
        this.help = "event roll commands";
        this.children = new SlashCommand[]
                {
                        new AddEvent(),
                        new DeleteEvent(),
                        new RollEvent()
                };
    }
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {

    }
    private static class AddEvent extends SlashCommand {
        public AddEvent() {
            this.name = "add";
            this.help = "Add a new event";
            this.options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "event_type", "type of event to add").setRequired(true));
            options.add(new OptionData(OptionType.STRING, "event", "event to add").setRequired(true));
            options.add(new OptionData(OptionType.STRING, "output", "result of the event").setRequired(true));
            this.userPermissions = new Permission[] {Permission.ADMINISTRATOR};
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply(true).queue();
            String eventType = Objects.requireNonNull(event.getOption("event_type")).getAsString();
            String eventString =  Objects.requireNonNull(event.getOption("event")).getAsString();
            String eventOutput = Objects.requireNonNull(event.getOption("output")).getAsString();
            EventRoll eventRoll = new EventRoll();
            eventRoll.setEvent(eventString);
            eventRoll.setEventtype(eventType);
            eventRoll.setEventoutput(eventOutput);
            EventRollHelper.SaveEvent(eventRoll);
            event.getHook().sendMessage("New event has been saved.").queue();
        }
    }

    private static class DeleteEvent extends SlashCommand {
        public DeleteEvent() {
            this.name = "delete";
            this.help = "Delete an event";
            this.options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER, "event_id", "ID of the event to delete").setRequired(true));
            this.userPermissions = new Permission[] {Permission.ADMINISTRATOR};
        }
        @Override
        protected void execute(SlashCommandEvent event) {
            long eventId = Objects.requireNonNull(event.getOption("event_id")).getAsLong();
            EventRollHelper.DeleteEvent(eventId);
            event.reply("Event deleted").setEphemeral(true).queue();
        }
    }

    private static class ListEvents extends SlashCommand {
        public ListEvents() {
            this.name = "list";
            this.help = "list all events";
            this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            List<EventRoll> eventRolls = EventRollHelper.ReturnAllEvents();
            if (eventRolls.isEmpty()) {
                event.reply("There are currently no events")
                        .setEphemeral(true)
                        .queue();
            } else {
                List<MessageEmbed> eventRollEmbeds = returnEventRollEmbeds(eventRolls, event.getUser());
                ButtonEmbedPaginator buttonEmbedPaginator = returnButtonEmbedPaginator(eventRollEmbeds);
                try {
                    event.replyEmbeds(new EmbedBuilder().setDescription("Checking for event rolls...").build())
                            .queue(hook -> hook.retrieveOriginal()
                                    .queue(message -> buttonEmbedPaginator.paginate(message, 0)));
                } catch (IllegalArgumentException ex) {
                    event.reply(ex.getMessage()).setEphemeral(true).queue();
                }
            }
        }

        private static List<MessageEmbed> returnEventRollEmbeds(List<EventRoll> eventRolls, User user) {
            List<MessageEmbed> events = new ArrayList<>();
            eventRolls.forEach(e -> {
                MessageEmbed embed = new EmbedBuilder()
                        .setTitle("ID - " + e.getId())
                        .setDescription(e.getEvent())
                        .addField(new MessageEmbed.Field("Output", e.getEventoutput(),false))
                        .addField(new MessageEmbed.Field("Type", e.getEventtype(), false))
                        .build();
                events.add(embed);
            });
            return events;
        }

        private static ButtonEmbedPaginator returnButtonEmbedPaginator(List<MessageEmbed> items) {
            return new ButtonEmbedPaginator.Builder()
                    .addItems(items)
                    .waitOnSinglePage(true)
                    .setTimeout(1, TimeUnit.MINUTES)
                    .setEventWaiter(EventRollCommands.eWaiter)
                    .build();
        }
    }

    private static class RollEvent extends SlashCommand {
        public RollEvent() {
            this.name = "roll";
            this.help = "Roll an event";
            this.options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "event_type", "Type of event").setRequired(true));
            options.add(new OptionData(OptionType.USER, "user", "user to roll event for"));
        }
        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply(false).queue();
            String eventType = Objects.requireNonNull(event.getOption("event_type")).getAsString();
            User eventTarget = null;
            if (event.hasOption("user")) {
                eventTarget = Objects.requireNonNull(event.getOption("user")).getAsUser();
            }
            List<EventRoll> eventRolls = EventRollHelper.ReturnEventsForType(eventType);
            if (eventRolls.isEmpty()) {
                event.getHook().sendMessage("There are no events for this type").queue();
                return;
            }
            EventRoll randomEvent = eventRolls.get(new Random().nextInt(eventRolls.size()));
            if (eventTarget != null) {
                event.getHook().sendMessage(eventTarget.getAsMention() + " " + randomEvent.getEventoutput()).queue();
            } else {
                event.getHook().sendMessage(randomEvent.getEventoutput()).queue();
            }
        }
    }
}
