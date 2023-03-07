create table member
(
    id                  bigint auto_increment comment 'id'
        primary key,
    member_token        varchar(255)               not null comment '사용자 Token',
    email               varchar(255)               not null comment '사용자 email',
    password            varchar(255)               null comment '사용자 password',
    first_name          varchar(128)               null comment '이름',
    last_name           varchar(128)               null comment '성',
    authentication_code varchar(30)                null comment '인증코드',
    status              varchar(30) default 'INIT' not null comment '회원 상태',
    auth_type           varchar(20)                not null comment '회원 타입',
    reason_withdrawal   varchar(255)               null comment '회원 탈퇴 사유',
    authority           varchar(30)                null comment '권한',
    created_at          datetime(6)                not null comment '생성일시',
    updated_at          datetime(6)                not null comment '수정일시',

    constraint uq_email_member_token_status
        unique (email, member_token, status),

    constraint uq_member_token
        unique (member_token)
);
