package com.villchurch.eponabot.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "movies", schema = "eponaRibbon")
public class Movies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "movie")
    private String movie;

    @Column(name = "requestedby")
    private String requestedby;

    @Column(name = "watched")
    private boolean watched;

    @Column(name = "genre")
    private String genre;
}
