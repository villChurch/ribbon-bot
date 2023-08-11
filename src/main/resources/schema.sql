CREATE SCHEMA IF NOT EXISTS eponaRibbon AUTHORIZATION postgres;

CREATE TABLE IF NOT EXISTS eponaRibbon.ribbon (
    id bigserial PRIMARY KEY,
    name text NOT NULL,
    description text,
    path text NOT NULL
);

CREATE TABLE IF NOT EXISTS eponaRibbon.userribbon (
    id bigserial PRIMARY KEY,
    userid text NOT NULL,
    ribbonid INT,
    FOREIGN KEY (ribbonid)
        REFERENCES eponaRibbon.ribbon (id)
);

CREATE TABLE IF NOT EXISTS eponaRibbon.user (
    id bigserial PRIMARY KEY,
    userid text NOT NULL,
    effectivename text NOT NULL
);

CREATE TABLE IF NOT EXISTS eponaRibbon.sticky (
    id bigserial PRIMARY KEY,
    channelid text NOT NULL,
    guildid text NOT NULL,
    message text NOT NULL,
    messageid text
);

CREATE TABLE IF NOT EXISTS  eponaRibbon.eyespy (
    id bigserial PRIMARY KEY,
    userid text NOT NULL,
    points INT
);

CREATE TABLE IF NOT EXISTS eponaRibbon.tags (
    id bigserial PRIMARY KEY,
    guildid text,
    tag text NOT NULL,
    tagtext text NOT NULL,
    userid text NOT NULL
);

CREATE TABLE IF NOT EXISTS eponaRibbon.eyespyawards (
    id bigserial PRIMARY KEY,
    points int,
    ribbonid int
);

CREATE TABLE IF NOT EXISTS eponaRibbon.pets (
    id bigserial PRIMARY KEY,
    childlink text,
    adultlink text
);

CREATE TABLE IF NOT EXISTS eponaRibbon.userpets (
    id bigserial PRIMARY KEY,
    petid INTEGER,
    owner text,
    name text,
    adult BOOLEAN DEFAULT false,
    FOREIGN KEY (petid)
            REFERENCES eponaRibbon.pets (id)
);

CREATE TABLE IF NOT EXISTS eponaRibbon.petsbuttons (
    id bigserial PRIMARY KEY,
    petid INTEGER,
    button text,
    msgid text,
    channelid text,
    guildid text,
    FOREIGN KEY (petid)
        REFERENCES eponaRibbon.pets (id)
);

CREATE TABLE IF NOT EXISTS eponaRibbon.qod (
    id bigserial PRIMARY KEY,
    question text,
    posted BOOLEAN DEFAULT false
);

CREATE TABLE IF NOT EXISTS eponaRibbon.movies (
    id bigserial PRIMARY KEY,
    movie text,
    requestedby text,
    watched BOOLEAN DEFAULT false,
    genre text
);