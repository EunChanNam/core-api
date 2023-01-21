CREATE TABLE member (
    id                          bigint auto_increment primary key comment 'id',
    member_token                varchar(255) not null comment '사용자 Token',
    email                       varchar(255) not null comment '사용자 email',
    password                    varchar(255) not null comment '사용자 password',
    first_name                  varchar(128) not null comment '이름',
    last_name                   varchar(128) not null comment '성',
    authentication_code         varchar(30) null comment '인증코드',
    status                      varchar(30) not null default 'INIT' comment '회원 상태',
    auth_type                   varchar(20) not null comment '회원 타입',
    reason_withdrawal           varchar(255) null comment '회원 탈퇴 사유',
    authority                   varchar(30) not null comment '권한',
    created_at                  datetime(6) not null comment '생성일시',
    updated_at                  datetime(6) not null comment '수정일시'
);

ALTER TABLE member ADD CONSTRAINT `uq_member_token` UNIQUE(member_token);
ALTER TABLE member ADD CONSTRAINT `uq_email_auth_type` UNIQUE(member_token, auth_type);