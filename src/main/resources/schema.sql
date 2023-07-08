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
)