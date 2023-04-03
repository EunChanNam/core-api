-- auto-generated definition
create table content
(
    id                  bigint auto_increment
        primary key,
    created_at          datetime(6)  null,
    updated_at          datetime(6)  null,
    content_token       varchar(255) null,
    category            varchar(255) null,
    price               bigint       null,
    price_policy        varchar(255) null,
    reading_time        varchar(255) null,
    content_media_type  varchar(255) null,
    author              varchar(255) null,
    book_image_url      varchar(255) null,
    second_title        varchar(255) null,
    channel_name        varchar(255) null,
    video_title         varchar(255) null,
    video_url           varchar(255) null,
    thumbnail_image_url varchar(255) null,
    member_id           bigint not null,

    constraint fk_member_id foreign key (member_id) references member (id)
    constraint uq_article_token
        unique (article_token)
);


-- auto-generated definition
create table chapter
(
    id              bigint auto_increment
        primary key,
    created_at      datetime(6)  null,
    updated_at      datetime(6)  null,
    cover_image_url varchar(255) null,
    overview        varchar(255) null,
    title           varchar(255) null,
    detail          text         null,
    ordering        int          null,
    time_flag       varchar(255) null,
    content_id       bigint       null,
    constraint FKpmljh5n5no8dg76rt28ek0ybx
        foreign key (content_id) references content (id)
);


