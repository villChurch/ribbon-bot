package com.villchurch.eponabot.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "birthdays", schema = "eponaribbon")
public class Birthday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user")
    private String user;

    @Column(name = "month")
    private Integer month;

    @Column(name = "day")
    private Integer day;
}
