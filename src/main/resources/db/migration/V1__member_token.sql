CREATE TABLE IF NOT EXISTS member_refresh_token (
    id bigint auto_increment primary key,
    member_token  varchar(255) not null,
    refresh_token varchar(255) not null,
    created_at    datetime(6) not null,
    updated_at    datetime(6) not null,
    FOREIGN KEY (member_token) REFERENCES member(member_token)
);