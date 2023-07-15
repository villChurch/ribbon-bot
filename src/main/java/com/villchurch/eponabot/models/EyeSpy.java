package com.villchurch.eponabot.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "eyespy", schema = "eponaribbon")
public class EyeSpy {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column(name = "userid")
    private String userid;

    @Column(name = "points")
    private int points;
}
