package com.learncha.api.auth.repository;

import static com.learncha.api.auth.domain.QMember.member;

import com.learncha.api.auth.domain.Member;
import com.learncha.api.auth.domain.Member.Status;
import com.mysema.commons.lang.Assert;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueryDslMemberRepositoryImpl implements QueryDslMemberRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findByEmailAndStatusIs(String email, Status status) {
        Assert.notNull(email, "email is never null");

        Member data = queryFactory.selectFrom(member)
            .where(member.email.eq(email).and(member.status.eq(status)))
            .fetchOne();

        return Optional.ofNullable(data);
    }

    @Override
    public Optional<Member> findByEmailAndStatusIsActive(String email) {
        Assert.notNull(email, "email is never null");

        Member data = queryFactory.selectFrom(member)
            .where(member.email.eq(email).and(member.status.eq(Status.ACTIVE)))
            .fetchOne();

        return Optional.ofNullable(data);
    }

    @Override
    public Optional<Member> findByEmailAndStatusIsNotDeleted(String email) {
        Assert.notNull(email, "email is never null");

        Member data = queryFactory.selectFrom(member)
            .where(member.email.eq(email).and(member.status.ne(Status.DELETED)))
            .fetchOne();

        return Optional.ofNullable(data);
    }
}
