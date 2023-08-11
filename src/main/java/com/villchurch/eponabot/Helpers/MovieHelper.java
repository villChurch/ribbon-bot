package com.villchurch.eponabot.Helpers;

import com.villchurch.eponabot.Repositories.MovieRepository;
import com.villchurch.eponabot.models.Movies;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieHelper {

    @Autowired
    MovieRepository getMovieRepository;

    private static MovieRepository movieRepository;

    @PostConstruct
    public void init() {
        movieRepository = getMovieRepository;
    }

    public static List<Movies> getAllMovies() {
        return movieRepository.findAll();
    }

    public static void saveMoive(Movies movie) {
        movieRepository.save(movie);
    }

}
