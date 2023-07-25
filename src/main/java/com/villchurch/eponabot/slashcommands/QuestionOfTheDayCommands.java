package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.villchurch.eponabot.Helpers.QodHelper;
import com.villchurch.eponabot.models.Qod;
import net.dv8tion.jda.api.Permission;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class QuestionOfTheDayCommands extends SlashCommand {

    public QuestionOfTheDayCommands() {
        this.name = "qod";
        this.help = "question of the day commands";
        this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        this.children = new SlashCommand[] {
                new Load(),
                new Test()
        };
    }
    @Override
    protected void execute(SlashCommandEvent event) {

    }

    private static class Test extends SlashCommand {
        public Test() {
            this.name = "test";
            this.help = "test post a daily question";
            this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            List<String> questions = QodHelper.returnQodList()
                    .stream().map(Qod::getQuestion).collect(Collectors.toList());
            int questionNumber = new Random().nextInt(questions.size());
            event.reply(questions.get(questionNumber)).queue();
        }
    }
    private static class Load extends SlashCommand {
        public Load() {
            this.name = "load";
            this.help = "loads questions from file into the database.";
            this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply(true).queue();
            List<String> currentQuestions = QodHelper.returnQodList()
                    .stream().map(Qod::getQuestion).collect(Collectors.toList());
            List<String> questions;
            try {
                 questions = Files.readAllLines(Paths.get("questions.txt"));
            } catch (IOException e) {
                event.getHook().sendMessage(e.getMessage()).queue();
                throw new RuntimeException(e);
            }
            List<Qod> newQuestions = new ArrayList<>();
            questions.removeAll(currentQuestions);
            questions.forEach(q -> {
                Qod qod = new Qod();
                qod.setQuestion(q);
                qod.setPosted(false);
                newQuestions.add(qod);
            });
            newQuestions.forEach(QodHelper::saveQod);
            event.getHook().sendMessage("Added " + newQuestions.size() + " questions.").queue();
        }
    }
}
