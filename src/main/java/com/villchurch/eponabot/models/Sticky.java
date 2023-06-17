package com.villchurch.eponabot.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sticky", schema = "eponaribbon")
public class Sticky {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="channelid")
    private String channelid;

    @Column(name="guildid")
    private String guildid;

    @Column(name="message")
    private String message;

    @Column(name="messageid")
    private String messageid;

}
