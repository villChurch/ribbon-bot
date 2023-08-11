package com.villchurch.eponabot.slashcommands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.menu.ButtonEmbedPaginator;
import com.villchurch.eponabot.EponaBotApplication;
import com.villchurch.eponabot.Helpers.MovieHelper;
import com.villchurch.eponabot.Repositories.MovieRepository;
import com.villchurch.eponabot.models.Movies;
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
import java.util.stream.Collectors;

public class MovieCommands extends SlashCommand {

    public MovieCommands() {
        this.name = "movie";
        this.help = "movie commands";
        this.children = new SlashCommand[] {
                new AddMovie(),
                new WatchMovie(),
                new ShowMovies()
        };
    }

    @Override
    protected void execute(SlashCommandEvent event) {

    }

    private static class ShowMovies extends SlashCommand {
        public ShowMovies() {
            this.name = "show";
            this.help = "show movies";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.BOOLEAN, "watch_status", "can be true or false for filtering")
                    .setRequired(false));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            boolean watchFilter = event.hasOption("watch_status");
            List<Movies> movies;
            if(watchFilter) {
                boolean watchStatus = Objects.requireNonNull(event.getOption("watch_status")).getAsBoolean();
                movies = MovieHelper.getAllMovies().stream().filter(m -> m.isWatched() == watchStatus).collect(Collectors.toList());
            } else {
                movies = MovieHelper.getAllMovies();
            }

            if(movies.isEmpty()) {
                event.reply("There are no movies that meet this criteria.").queue();
            } else  {
                ButtonEmbedPaginator buttonEmbedPaginator = returnButtonEmbedPagiator(returnMoviesEmbeds(movies));
                try {
                    event.replyEmbeds(new EmbedBuilder().setDescription("Checking for movies...").build())
                            .queue(interactionHook -> interactionHook.retrieveOriginal()
                                    .queue(message -> buttonEmbedPaginator.paginate(message, 0)));
                } catch (IllegalArgumentException ex) {
                    event.reply(ex.getMessage()).queue();
                }
            }
        }
    }

    private static List<MessageEmbed> returnMoviesEmbeds(List<Movies> movies) {
        List<MessageEmbed> embeds = new ArrayList<>();
        movies.forEach(movie -> {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle(movie.getMovie())
                    .addField(new MessageEmbed.Field("Genre", movie.getGenre(), false))
                    .addField(new MessageEmbed.Field("watched", movie.isWatched() ? "Yes" : "No", false))
                    .addField(new MessageEmbed.Field("Requested By", movie.getRequestedby(), false))
                    .build();
            embeds.add(embed);
        });
        return embeds;
    }

    private static ButtonEmbedPaginator returnButtonEmbedPagiator(List<MessageEmbed> items) {
        return new ButtonEmbedPaginator.Builder()
                .addItems(items)
                .waitOnSinglePage(true)
                .setTimeout(1, TimeUnit.MINUTES)
                .setEventWaiter(EponaBotApplication.eWaiter)
                .build();
    }

    private static class AddMovie extends SlashCommand {
        public AddMovie() {
            this.name = "request";
            this.help = "request a movie to be shown";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "title", "title of the movie you want to request")
                    .setRequired(true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply().setEphemeral(true).queue();
            String movieName = Objects.requireNonNull(event.getOption("title")).getAsString();
            List<Movies> movies = MovieHelper.getAllMovies();
            if (movies.stream().anyMatch(m -> (m.getMovie().equalsIgnoreCase(movieName)) && !m.isWatched())) {
                event.getHook().sendMessage("This movie is already in the request queue.").queue();
            } else {
                Movies movie = new Movies();
                movie.setMovie(movieName);
                movie.setRequestedby(event.getUser().getId());
                movie.setGenre("Community");
                MovieHelper.saveMoive(movie);
                event.getHook().sendMessage(movieName + " has been successfully requested.").queue();
            }
        }
    }

    private static class WatchMovie extends SlashCommand {
        public WatchMovie() {
            this.name = "watch";
            this.help = "set a movies watch status";
            this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.BOOLEAN, "watch_status", "whether the movie has been watched")
                    .setRequired(true));
            options.add(new OptionData(OptionType.STRING, "title", "title of the movie")
                    .setRequired(true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply().setEphemeral(true).queue();
            String movieName = Objects.requireNonNull(event.getOption("title")).getAsString();
            List<Movies> movies = MovieHelper.getAllMovies();
            Optional<Movies> moviesOptional = movies.stream().filter(m -> m.getMovie().equalsIgnoreCase(movieName)).findFirst();
            if (moviesOptional.isEmpty()) {
                event.getHook().sendMessage("No movie found by name " + movieName).queue();
            } else {
                Movies movie = moviesOptional.get();
                movie.setWatched(Objects.requireNonNull(event.getOption("watch_status")).getAsBoolean());
                MovieHelper.saveMoive(movie);
                event.getHook().sendMessage("Movie watch status updated for " + movieName).queue();
            }
        }
    }
}
