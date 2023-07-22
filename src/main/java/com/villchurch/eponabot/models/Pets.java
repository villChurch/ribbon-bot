package com.villchurch.eponabot.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "pets", schema = "eponaRibbon")
public class Pets {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column(name="childlink")
    private String childlink;

    @Column(name="adultlink")
    private String adultlink;
}
