package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonEmbedPaginator;
import com.villchurch.eponabot.EponaBotApplication;
import com.villchurch.eponabot.Helpers.PetHelper;
import com.villchurch.eponabot.models.Pets;
import com.villchurch.eponabot.models.PetsButtons;
import com.villchurch.eponabot.models.Userpets;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                new ListPets(),
                new SpawnPet(),
                new DeSpawnPet(),
                new DeletePet(),
                new ReleasePet(),
                new TradePet(),
                new SpawnRandomPet()
        };
    }
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {

    }

    public static class TradePet extends SlashCommand {
        public TradePet() {
            this.name = "trade";
            this.help = "this will allow you to trade this pet to another user";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER, "pet_id", "id of pet to trade")
                    .setRequired(true));
            options.add(new OptionData(OptionType.USER, "user", "user to trade the pet with")
                    .setRequired(true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply().queue();
            long petId = Objects.requireNonNull(event.getOption("pet_id")).getAsLong();
            List<Userpets> pets = PetHelper.getUsersPetByUserId(event.getUser().getId());
            if (pets.isEmpty()) {
                event.getHook().sendMessage("You currently do not own any pets").queue();
            } else if (pets.stream().anyMatch(p -> p.getId() == petId)) {
                Userpets pet = pets.stream().filter(p -> p.getId() == petId).collect(Collectors.toList()).get(0);
                User user = Objects.requireNonNull(event.getOption("user")).getAsUser();
                pet.setOwner(user.getId());
                PetHelper.saveUsersPet(pet);
                event.getHook().sendMessage(pet.getName() + " has now been transferred to " + user.getName()).queue();
            } else {
                event.getHook().sendMessage("You do not own a pet with id " + petId).queue();
            }
        }
    }

    public static class ReleasePet extends SlashCommand {
        public ReleasePet() {
            this.name = "release";
            this.help = "this will release a pet you own and remove it from your collection.";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER, "pet_id", "id of pet to release")
                    .setRequired(true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply(true).queue();
            long petId = Objects.requireNonNull(event.getOption("pet_id")).getAsLong();
            List<Userpets> pets = PetHelper.getUsersPetByUserId(event.getUser().getId());
            if (pets.isEmpty()) {
                event.getHook().sendMessage("You currently do not own any pets.").queue();
            } else if (pets.stream().anyMatch(p -> p.getId() == petId)) {
                Userpets pet = pets.stream().filter(p -> p.getId() == petId).collect(Collectors.toList()).get(0);
                PetHelper.removePetFromUser(pet);
                event.getHook().sendMessage("Your pet " + pet.getName() + " has been released.").queue();
            } else {
                event.getHook().sendMessage("You do not own a pet with id " + petId).queue();
            }
        }
    }

    public static class DeletePet extends SlashCommand {
        public DeletePet() {
            this.name = "delete";
            this.help = "this will delete a pet and remove it from everybody";
            this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER, "pet_id", "id of pet to delete")
                    .setRequired(true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply(true).queue();
            long petId = Objects.requireNonNull(event.getOption("pet_id")).getAsLong();
            Optional<Pets> optional = PetHelper.getPetById(petId);
            if (optional.isEmpty()) {
                event.getHook().sendMessage("There are no pets with id " + petId).queue();
            } else {
                List<Userpets> userpetsList = PetHelper.findByUserpetsByPetId(petId);
                userpetsList.forEach(PetHelper::removePetFromUser);
                RemovePetSpawn(PetHelper.findPetButtonsByPetId(petId), event);
                PetHelper.deletePet(optional.get());
                event.getHook().sendMessage("Deleted pet with id " + petId + ". " + userpetsList.size() + " pets removed from users.")
                        .queue();
            }
        }
    }

    private static void RemovePetSpawn(List<PetsButtons> buttons, SlashCommandEvent event) {
        if (!buttons.isEmpty()) {
            buttons.forEach(button -> {
                var guild = event.getJDA().getGuildById(button.getGuildid());
                var channel = Objects.requireNonNull(guild).getTextChannelById(button.getChannelid());
                Objects.requireNonNull(channel).deleteMessageById(button.getMsgid()).queue();
            });
            PetHelper.deletePetsButtons(buttons);
        }
    }

    public static class DeSpawnPet extends SlashCommand {
        public DeSpawnPet() {
            this.name = "despawn";
            this.help = "Despawn a pet";
            this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.INTEGER, "pet_id", "id of pet to despawn")
                    .setRequired(true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply(true).queue();
            long petId = Objects.requireNonNull(event.getOption("pet_id")).getAsLong();
            List<PetsButtons> buttons = PetHelper.findPetButtonsByPetId(petId);
            if (buttons.isEmpty()) {
                event.getHook().sendMessage("No buttons for pet id " + petId).queue();
            } else {
                RemovePetSpawn(buttons, event);
                event.getHook().sendMessage(buttons.size() + " messages and button listeners removed.").queue();
            }
        }
    }

    public static class SpawnRandomPet extends SlashCommand {
        public SpawnRandomPet() {
            this.name = "spawn_random";
            this.help = "Spawn a random pet";
            this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.CHANNEL, "channel", "Channel to write spawn message in")
                    .setRequired(true));
            options.add(new OptionData(OptionType.STRING, "pet_ids", "comma separated list of pet id's")
                    .setRequired(true));
            options.add(new OptionData(OptionType.STRING, "message", "Optional message to post with the pet spawn")
                    .setRequired(true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply(true).queue();
            String idsString = Objects.requireNonNull(event.getOption("pet_ids")).getAsString();
            List<Integer> convertedPetIds = Stream.of(idsString.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            List<Long> validPetIds = new ArrayList<>();
            convertedPetIds.forEach(id -> {
                if (VerifyPetId(id)) {
                    validPetIds.add((long)id);
                }
            });
            if (validPetIds.isEmpty()) {
                event.getHook().sendMessage("None of the id's you entered are valid pet id's.")
                        .setEphemeral(true)
                        .queue();
            } else {
                Pets randomPet = PetHelper.getPetById(PetHelper.randomPetId).get();
                String message = Objects.requireNonNull(event.getOption("message")).getAsString();
                MessageEmbed embed = new EmbedBuilder()
                        .setTitle("A new pet has appeared!")
                        .setDescription(message)
                        .setImage(randomPet.getChildlink())
                        .build();
                var channel = Objects.requireNonNull(event.getOption("channel")).getAsChannel().asTextChannel();
                event.getHook().sendMessage("Pet spawned!").setEphemeral(true).queue();
                StringBuilder validPetIdsString = new StringBuilder();
                for(int i = 0; i < validPetIds.size(); i++) {
                    if (i + 1 == validPetIds.size()) {
                        validPetIdsString.append(validPetIds.get(i));
                    } else {
                        validPetIdsString.append(validPetIds.get(i));
                        validPetIdsString.append(",");
                    }
                }
                channel.sendMessageEmbeds(embed)
                        .addActionRow(
                                Button.primary("random_pet" + validPetIdsString, "Claim")
                        )
                        .queue(message1 -> {
                            PetsButtons petsButtons = new PetsButtons();
                            petsButtons.setPetid(PetHelper.randomPetId);
                            petsButtons.setButton("random_pet" + validPetIdsString);
                            petsButtons.setMsgid(message1.getId());
                            petsButtons.setChannelid(message1.getChannel().getId());
                            petsButtons.setGuildid(message1.getGuild().getId());
                            PetHelper.savePetButton(petsButtons);
                        });
            }
        }
    }

    public static boolean VerifyPetId(Integer petId) {
        Optional<Pets> optional = PetHelper.getPetById((long)petId);
        return optional.isPresent();
    }

    public static class SpawnPet extends SlashCommand {
        public SpawnPet() {
            this.name = "spawn";
            this.help = "Spawn a new pet";
            this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.CHANNEL, "channel", "Channel to write spawn message")
                    .setRequired(true));
            options.add(new OptionData(OptionType.INTEGER, "pet_id", "id of pet to spawn")
                    .setRequired(true));
            options.add(new OptionData(OptionType.STRING, "message", "Optional message to post with the pet spawn"));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply(true).queue();
            long petId = Objects.requireNonNull(event.getOption("pet_id")).getAsLong();
            Optional<Pets> optional = PetHelper.getPetById(petId);
            if (optional.isEmpty()) {
                event.getHook().sendMessage("No pet found with id " + petId).queue();
            } else {
                Pets pet  = optional.get();
                var channel = Objects.requireNonNull(event.getOption("channel")).getAsChannel().asTextChannel();
                String msg = "Click claim to claim this pet";
                if (event.hasOption("message")) {
                    msg = Objects.requireNonNull(event.getOption("message")).getAsString();
                }
                event.getHook().sendMessage("Pet spawned!").queue();
                MessageEmbed embed = new EmbedBuilder()
                        .setTitle("A new pet has appeared!")
                        .setDescription(msg)
                        .setImage(pet.getChildlink())
                        .build();
                channel.sendMessageEmbeds(embed)
                        .addActionRow(
                                Button.primary("claim" + petId, "Claim")
                        )
                        .queue(message ->  {
                            PetsButtons petsButtons = new PetsButtons();
                            petsButtons.setPetid(petId);
                            petsButtons.setButton("claim" + petId);
                            petsButtons.setMsgid(message.getId());
                            petsButtons.setChannelid(message.getChannel().getId());
                            petsButtons.setGuildid(message.getGuild().getId());
                            PetHelper.savePetButton(petsButtons);
                        });
            }
        }
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
            options.add(new OptionData(OptionType.STRING, "child_link", "location to child variant image").setRequired(true));
            options.add(new OptionData(OptionType.STRING, "adult_link","location to adult variant image").setRequired(true));
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
            options.add(new OptionData(OptionType.INTEGER, "id", "id of pet to assign").setRequired(true));
            options.add(new OptionData(OptionType.USER, "user", "user to assign pet to").setRequired(true));
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
            options.add(new OptionData(OptionType.INTEGER, "id", "id of pet to change name")
                    .setRequired(true));
            options.add(new OptionData(OptionType.STRING, "name", "name for your pet")
                    .setRequired(true));
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
