--liquibase formatted sql
--changeset sysoiev:1

create table if not exists comments
(
    id         bigint not null auto_increment,
    content    TEXT,
    created_at datetime(6),
    updated_at datetime(6),
    post_id    bigint,
    user_id    bigint,
    primary key (id)
) engine = InnoDB;

create table if not exists posts
(
    id         bigint not null auto_increment,
    content    MEDIUMTEXT,
    created_at datetime(6),
    title      varchar(255),
    updated_at datetime(6),
    topic_id   bigint,
    user_id    bigint,
    primary key (id)
) engine = InnoDB;

create table if not exists topics
(
    id         bigint not null auto_increment,
    created_at datetime(6),
    name       varchar(20),
    updated_at datetime(6),
    primary key (id)
) engine = InnoDB;

create table if not exists users
(
    id             bigint       not null auto_increment,
    account_status varchar(255),
    bio            varchar(255),
    created_at     datetime(6),
    email          varchar(255),
    first_name     varchar(30),
    last_name      varchar(30),
    password       varchar(255) not null,
    updated_at     datetime(6),
    user_role      varchar(255),
    username       varchar(20),
    primary key (id)
) engine = InnoDB;

alter table users
    add constraint UK_6dotkott2kjsp8vw4d0m25fb7 unique (email);

alter table users
    add constraint UK_r43af9ap4edm43mmtq01oddj6 unique (username);

alter table comments
    add constraint FKh4c7lvsc298whoyd4w9ta25cr foreign key (post_id) references posts (id);

alter table comments
    add constraint FK8omq0tc18jd43bu5tjh6jvraq foreign key (user_id) references users (id);

alter table posts
    add constraint FKrfchr8dax0kfngvvkbteh5n7h foreign key (topic_id) references topics (id);

alter table posts
    add constraint FK5lidm6cqbc7u4xhqpxm898qme foreign key (user_id) references users (id);
