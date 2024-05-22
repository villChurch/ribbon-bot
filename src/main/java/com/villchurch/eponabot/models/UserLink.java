package com.villchurch.eponabot.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "userlink", schema = "eponaRibbon")
public class UserLink {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column(name = "discordid")
    private String discordid;

    @Column(name = "code")
    private String code;
}
