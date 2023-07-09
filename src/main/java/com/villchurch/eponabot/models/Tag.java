package com.villchurch.eponabot.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tags", schema = "eponaRibbon")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "guildid")
    private String guildid;

    @Column(name = "tag")
    private String tag;

    @Column(name = "userid")
    private String userid;

    @Column(name = "tagtext")
    private String tagtext;
}
