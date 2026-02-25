package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.villchurch.eponabot.Helpers.ConfigHelper;
import com.villchurch.eponabot.models.Config;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ConfigCommands extends SlashCommand {

    public ConfigCommands() {
        this.name = "config";
        this.help = "Config commands";
        this.userPermissions = new Permission[] {
                Permission.ADMINISTRATOR
        };
        this.children = new SlashCommand[] {
                new AddConfig(),
                new EditConfig(),
                new DeleteConfig(),
                new ListConfig()
        };
    }
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {

    }

    public static class ListConfig extends SlashCommand {
        public ListConfig() {
            this.name = "list";
            this.help = "show all config values";
            this.userPermissions = new Permission[] {
                    Permission.ADMINISTRATOR
            };
        }

        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {

        }
    }

    public static class DeleteConfig extends  SlashCommand {
        public DeleteConfig() {
            this.name = "delete";
            this.help = "delete a config";
            this.userPermissions = new Permission[] {
                    Permission.ADMINISTRATOR
            };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER, "id", "id of config value to delete").setRequired(true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply().setEphemeral(true).queue();
            long id = Objects.requireNonNull(event.getOption("id")).getAsLong();
            Optional<Config> config = ConfigHelper.GetConfigById(id);
            if(config.isEmpty()) {
                event.getHook().sendMessage("No config value found with id - " + id).queue();
            } else {
                ConfigHelper.DeleteConfig(config.get());
                event.getHook().sendMessage("Config value delete with id - " + id).queue();
            }
        }
    }

    public static class EditConfig extends SlashCommand {
        public EditConfig() {
            this.name = "edit";
            this.help = "edit a config value";
            this.userPermissions = new Permission[] {
                    Permission.ADMINISTRATOR
            };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER, "id", "id of option to edit").setRequired(true));
            options.add(new OptionData(OptionType.STRING, "value", "updated value for the option").setRequired(true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply().setEphemeral(true).queue();
            long id = Objects.requireNonNull(event.getOption("id")).getAsLong();
            Optional<Config> config = ConfigHelper.GetConfigById(id);
            if(config.isEmpty()) {
                event.getHook().sendMessage("No config item found with id - " + id).queue();
            } else {
                config.get().setValue(Objects.requireNonNull(event.getOption("value")).getAsString());
                ConfigHelper.SaveConfig(config.get());
                event.getHook().sendMessage("Value updated for config with id - " + id).queue();
            }
        }
    }
    public static class AddConfig extends SlashCommand {
        public AddConfig() {
            this.name = "add";
            this.help = "add new config value";
            this.userPermissions = new Permission[] {
                    Permission.ADMINISTRATOR
            };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "description", "Description of the config command"));
            options.add(new OptionData(OptionType.STRING, "name", "Name of config option").setRequired(true));
            options.add(new OptionData(OptionType.STRING, "value", "value of config option").setRequired(true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply().setEphemeral(true).queue();
            Config newConfig = new Config();
            newConfig.setName(Objects.requireNonNull(event.getOption("name")).getAsString());
            newConfig.setValue(Objects.requireNonNull(event.getOption("value")).getAsString());
            if(event.hasOption("description")) {
                newConfig.setDescription(Objects.requireNonNull(event.getOption("description")).getAsString());
            }
            ConfigHelper.SaveConfig(newConfig);
            event.getHook().sendMessage("New config item has been added").queue();
        }
    }
}
