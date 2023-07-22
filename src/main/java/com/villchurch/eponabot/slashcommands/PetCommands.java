package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonEmbedPaginator;
import com.villchurch.eponabot.EponaBotApplication;
import com.villchurch.eponabot.Helpers.PetHelper;
import com.villchurch.eponabot.models.Pets;
import com.villchurch.eponabot.models.Userpets;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PetCommands extends SlashCommand {

    private static final EventWaiter eWaiter = EponaBotApplication.eWaiter;

    public PetCommands() {
        this.name = "pet";
        this.help = "pet commands";
        this.children = new SlashCommand[] {
                new Name(),
                new Assign(),
                new Add(),
                new Show(),
                new Remove(),
                new ListPets()
        };
    }
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {

    }

    public static class ListPets extends SlashCommand {
        public ListPets() {
            this.name = "list";
            this.help = "list all pets";
            this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            List<Pets> pets = PetHelper.returnAllPets();
            if (pets.isEmpty()) {
                slashCommandEvent.reply("There are currently no pets.").queue();
            } else {
                List<MessageEmbed> petsEmbed = returnPetEmbed(pets, slashCommandEvent.getUser());
                ButtonEmbedPaginator buttonEmbedPaginator = returnButtonEmbedPaginator(petsEmbed);
                try {
                    slashCommandEvent.replyEmbeds(new EmbedBuilder().setDescription("Checking for pets...").build())
                            .queue(interactionHook -> interactionHook.retrieveOriginal()
                                    .queue(message -> buttonEmbedPaginator.paginate(message, 0)));
                } catch (IllegalArgumentException ex) {
                    slashCommandEvent.reply(ex.getMessage()).queue();
                }
            }
        }
    }

    public static class Remove extends SlashCommand {
        public Remove() {
            this.name = "remove";
            this.help = "remove a pet from someone";
            this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER, "id", "id of pet to remove"));
            options.add(new OptionData(OptionType.USER, "user", "user to remove pet from"));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply().setEphemeral(true).queue();
            User user = Objects.requireNonNull(slashCommandEvent.getOption("user")).getAsUser();
            List<Userpets> userpets = PetHelper.getUsersPetByUserId(user.getId());
            if (userpets.isEmpty()) {
                slashCommandEvent.getHook().sendMessage("This user does not have any pets").queue();
                return;
            }
            long petIdToMatch = Objects.requireNonNull(slashCommandEvent.getOption("id")).getAsLong();
            List<Userpets> petsToRemove = userpets.stream()
                    .filter(p -> p.getPetid() == petIdToMatch)
                    .collect(Collectors.toList());
            if (petsToRemove.isEmpty()) {
                slashCommandEvent.getHook().sendMessage("This user does not have any pets with a pet id of " + petIdToMatch).queue();
            } else {
                petsToRemove.forEach(PetHelper::removePetFromUser);
                slashCommandEvent.getHook().sendMessage("Pet with pet id " + petIdToMatch + " removed from " + user.getName()).queue();
            }
        }
    }
    public static class Show extends SlashCommand {
        public Show() {
            this.name = "show";
            this.help = "show your pets";
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            List<Userpets> pets = PetHelper.getUsersPetByUserId(slashCommandEvent.getUser().getId());
            if (pets.isEmpty()) {
                slashCommandEvent.reply("You currently don't own any pets.").queue();
            } else {
                List<MessageEmbed> petsEmbed = returnPetsEmbed(pets, slashCommandEvent.getUser());
                ButtonEmbedPaginator buttonEmbedPaginator = returnButtonEmbedPaginator(petsEmbed);
                try {
                    slashCommandEvent.replyEmbeds(new EmbedBuilder().setDescription("Checking for pets...").build())
                            .queue(interactionHook -> interactionHook.retrieveOriginal()
                                    .queue(message -> buttonEmbedPaginator.paginate(message, 0)));
                } catch (IllegalArgumentException ex) {
                    slashCommandEvent.reply(ex.getMessage()).queue();
                }
            }
        }
    }

    private static ButtonEmbedPaginator returnButtonEmbedPaginator(List<MessageEmbed> items) {
        return new ButtonEmbedPaginator.Builder()
                .addItems(items)
                .waitOnSinglePage(true)
                .setTimeout(1, TimeUnit.MINUTES)
                .setEventWaiter(PetCommands.eWaiter)
                .build();
    }

    private static List<MessageEmbed> returnPetsEmbed(List<Userpets> userpets, User user) {
        List<MessageEmbed> pets = new ArrayList<>();
        Collections.reverse(userpets);
        userpets.forEach(p -> {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle(p.getName())
                    .setDescription("ID - " + p.getId())
                    .setImage(p.isAdult() ? PetHelper.returnAdultLinkFromPetId(p.getPetid())
                            : PetHelper.returnChildLinkFromPetId(p.getPetid()))
                    .setFooter(user.getName() + "'s Pets")
                    .build();
            pets.add(embed);
        });
        return  pets;
    }

    private static List<MessageEmbed> returnPetEmbed(List<Pets> petsList, User user) {
        List<MessageEmbed> pets = new ArrayList<>();
        Collections.reverse(petsList);
        petsList.forEach(p -> {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("ID - " + p.getId())
                    .addField(new MessageEmbed.Field("Child Link", p.getChildlink(), false))
                    .addField(new MessageEmbed.Field("Adult Link", p.getAdultlink(), false))
                    .build();
            pets.add(embed);
        });
        return  pets;
    }

    public static class Add extends SlashCommand {
        public Add() {
            this.name = "add";
            this.help = "add a new pet";
            this.userPermissions = new Permission[] {
                    Permission.ADMINISTRATOR
            };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "child_link", "location to child variant image"));
            options.add(new OptionData(OptionType.STRING, "adult_link","location to adult variant image"));
            this.options = options;
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply().setEphemeral(true).queue();
            Pets newPet = new Pets();
            newPet.setChildlink(Objects.requireNonNull(slashCommandEvent.getOption("child_link")).getAsString());
            newPet.setAdultlink(Objects.requireNonNull(slashCommandEvent.getOption("adult_link")).getAsString());
            Pets pet = PetHelper.savePets(newPet);
            slashCommandEvent.getHook().sendMessage("New pet saved with id " + pet.getId()).queue();
        }
    }
    public static class Assign extends SlashCommand {
        public Assign() {
            this.name = "assign";
            this.help = "assign a pet to someone";
            this.userPermissions = new Permission[] {
                    Permission.ADMINISTRATOR
            };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER, "id", "id of pet to assign"));
            options.add(new OptionData(OptionType.USER, "user", "user to assign pet to"));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply().setEphemeral(true).queue();
            User owner = Objects.requireNonNull(slashCommandEvent.getOption("user")).getAsUser();
            long petId = Objects.requireNonNull(slashCommandEvent.getOption("id")).getAsLong();
            Userpets pet = new Userpets();
            pet.setOwner(owner.getId());
            pet.setName("New Pet");
            pet.setPetid(petId);
            PetHelper.saveUsersPet(pet);
            slashCommandEvent.getHook().sendMessage("Pet added to " + owner.getName()).queue();
        }
    }

    public static class Name extends SlashCommand {
        public Name() {
            this.name = "name";
            this.help = "name your pet";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER, "id", "id of pet to change name"));
            options.add(new OptionData(OptionType.STRING, "name", "name for your pet"));
            this.options = options;
        }
        @Override
        protected void execute(SlashCommandEvent slashCommandEvent) {
            slashCommandEvent.deferReply().setEphemeral(true).queue();
            Long petId = Objects.requireNonNull(slashCommandEvent.getOption("id")).getAsLong();
            Optional<Userpets> optionalPet = PetHelper.getUsersPetById(petId);
            if (optionalPet.isEmpty()) {
                slashCommandEvent.getHook().sendMessage("No Pet found with id " + petId).queue();
                return;
            }
            Userpets pet = optionalPet.get();
            if (!Objects.equals(pet.getOwner(), slashCommandEvent.getUser().getId())) {
                slashCommandEvent.getHook().sendMessage("You do not own a pet with id " + petId).queue();
            } else {
                String newName = Objects.requireNonNull(slashCommandEvent.getOption("name")).getAsString();
                pet.setName(newName);
                PetHelper.saveUsersPet(pet);
                slashCommandEvent.getHook().sendMessage("Your pet has been renamed to " + newName).queue();
            }

        }
    }
}