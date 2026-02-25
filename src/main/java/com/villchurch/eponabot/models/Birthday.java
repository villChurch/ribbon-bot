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

    @Column(name = "userid")
    private String user;

    @Column(name = "monthI")
    private Integer month;

    @Column(name = "dayI")
    private Integer day;
}
