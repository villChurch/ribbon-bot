package com.villchurch.eponabot.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "eyespyawards", schema = "eponaRibbon")
public class EyeSpyAwards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "points")
    private int points;

    @Column(name = "ribbonid")
    private int ribbonid;
}
