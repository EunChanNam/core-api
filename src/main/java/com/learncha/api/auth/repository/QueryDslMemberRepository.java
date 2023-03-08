package com.learncha.api.auth.repository;

import com.learncha.api.auth.domain.Member;
import com.learncha.api.auth.domain.Member.Status;
import java.util.Optional;

public interface QueryDslMemberRepository {
    Optional<Member> findByEmailAndStatusIsActive(String email);
    Optional<Member> findByEmailAndStatusIsNotDeleted(String email);
}
