package com.villchurch.eponabot.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users", schema = "eponaribbon")
public class UserModel {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "role")
    private String role;

    @Column(name = "discordid")
    private String discordid;
}
