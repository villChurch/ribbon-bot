package com.villchurch.eponabot.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "petsbuttons", schema = "eponaRibbon")
public class PetsButtons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "petid")
    private long petid;

    @Column(name = "button")
    private String button;

    @Column(name = "msgid")
    private String msgid;

    @Column(name = "channelid")
    private String channelid;

    @Column(name = "guildid")
    private String guildid;
}
