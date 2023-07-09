package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.villchurch.eponabot.Helpers.TagHelper;
import com.villchurch.eponabot.models.Tag;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TagCommands extends SlashCommand {
    public TagCommands() {
        this.name = "tag";
        this.help = "tag commands";
        this.children = new SlashCommand[] {
                new AddTag(),
                new ShowTag(),
                new DeleteTag(),
                new UpdateTag()
        };
    }
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {

    }

    public static class AddTag extends SlashCommand {
        public AddTag() {
            this.name = "add";
            this.help = "add a new tag";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "name of the tag")
                    .setRequired(true));
            options.add(new OptionData(OptionType.STRING, "value", "value of the tag")
                    .setRequired(true));
            options.add(new OptionData(OptionType.BOOLEAN, "guild_only", "should the tag be guild only")
                    .setRequired(false));
            this.options = options;
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply(true).queue();
            String tagName = Objects.requireNonNull(slashCommandEvent.getOption("name")).getAsString();
            if (TagHelper.returnTag(tagName).isPresent()) {
                slashCommandEvent.getHook().sendMessage("A tag with name " + tagName +
                        " already exists.").queue();
                return;
            }
            boolean guildOnly = false;
            if (slashCommandEvent.hasOption("guild_only")) {
                guildOnly = Objects.requireNonNull(
                        slashCommandEvent.getOption("guild_only")).getAsBoolean();
            }
            Tag tag = new Tag();
            if (guildOnly) {
                tag.setGuildid(Objects.requireNonNull(slashCommandEvent.getGuild()).getId());
            }
            tag.setUserid(slashCommandEvent.getUser().getId());
            tag.setTag(tagName);
            tag.setTagtext(Objects.requireNonNull(slashCommandEvent.getOption("value")).getAsString());
            TagHelper.saveTag(tag);
            slashCommandEvent.getHook().sendMessage(
                            "Tag created with name " +
                                    Objects.requireNonNull(
                                            slashCommandEvent.getOption("name")).getAsString())
                    .queue();
        }
    }

    public static class ShowTag extends SlashCommand {
        public ShowTag() {
            this.name = "show";
            this.help = "show a tag";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "name of the tag to show")
                    .setRequired(true));
            this.options = options;
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply().queue();
            String tagName = Objects.requireNonNull(slashCommandEvent.getOption("name")).getAsString();
            Optional<Tag> tagOptional = TagHelper.returnTag(tagName);
            if (tagOptional.isEmpty()) {
                slashCommandEvent.getHook().sendMessage("No tag found with name - " + tagName)
                        .queue();
                return;
            }
            Tag tag = tagOptional.get();
            if (tag.getGuildid() == null) {
                slashCommandEvent.getHook().sendMessage(tag.getTagtext()).queue();
            } else if (!tag.getGuildid().equals(Objects.requireNonNull(slashCommandEvent.getGuild()).getId())) {
                slashCommandEvent.getHook().sendMessage("This tag cannot be displayed here")
                        .queue();
            } else {
                slashCommandEvent.getHook().sendMessage(tag.getTagtext()).queue();
            }
        }
    }

    public static class DeleteTag extends SlashCommand {
        public DeleteTag() {
            this.name = "delete";
            this.help = "delete a tag";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "name of tag to delete")
                    .setRequired(true));
            this.options = options;
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply(true).queue();
            String tagName = Objects.requireNonNull(slashCommandEvent.getOption("name")).getAsString();
            Optional<Tag> optionalTag = TagHelper.returnTag(tagName);
            if (optionalTag.isEmpty()) {
                slashCommandEvent.getHook().sendMessage("No tag found with name - " + tagName)
                        .queue();
                return;
            }
            Tag tag = optionalTag.get();
            if (slashCommandEvent.getUser().getId().equals(tag.getUserid()) ||
                    Objects.requireNonNull(
                            slashCommandEvent.getMember()).hasPermission(Permission.ADMINISTRATOR)) {
                TagHelper.deleteTag(tag);
                slashCommandEvent.getHook().sendMessage("Tag called " + tagName + " has been deleted")
                        .queue();
            } else {
                slashCommandEvent.getHook().sendMessage("You do not have permission to delete this tag")
                        .queue();
            }
        }
    }

    public static class UpdateTag extends SlashCommand {
        public UpdateTag() {
            this.name = "update";
            this.help = "update an existing tag";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "name of tag to update")
                    .setRequired(true));
            options.add(new OptionData(OptionType.STRING, "value", "value of the tag")
                    .setRequired(true));
            this.options = options;
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply(true).queue();
            String tagName = Objects.requireNonNull(slashCommandEvent.getOption("name")).getAsString();
            Optional<Tag> optionalTag = TagHelper.returnTag(tagName);
            if (optionalTag.isEmpty()) {
                slashCommandEvent.getHook().sendMessage("No tag found with name " + tagName)
                        .queue();
                return;
            }
            Tag tag = optionalTag.get();
            if (tag.getUserid().equals(slashCommandEvent.getUser().getId()) ||
                    Objects.requireNonNull(slashCommandEvent.getMember())
                            .hasPermission(Permission.ADMINISTRATOR)) {
                tag.setTagtext(Objects.requireNonNull(slashCommandEvent.getOption("value")).getAsString());
                TagHelper.saveTag(tag);
                slashCommandEvent.getHook().sendMessage("Tag has been updated.")
                        .queue();
            } else {
                slashCommandEvent.getHook().sendMessage("You do not have permission to update this tag.")
                        .queue();
            }
        }
    }
}
