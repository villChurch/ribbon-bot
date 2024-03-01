package com.villchurch.eponabot.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "eventRolls", schema = "eponaRibbon")
public class EventRoll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="eventtype")
    private String eventtype;

    @Column(name = "event")
    private String event;

    @Column(name = "eventoutput")
    private String eventoutput;
}
