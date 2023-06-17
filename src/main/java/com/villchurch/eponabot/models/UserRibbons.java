package com.villchurch.eponabot.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "userribbon", schema = "eponaribbon")
public class UserRibbons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "userid")
    private String userid;

    @Column(name = "ribbonid")
    private long ribbonid;
}
