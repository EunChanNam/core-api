package com.learncha.api.auth.repository;

import com.learncha.api.auth.domain.Member;
import com.learncha.api.auth.domain.Member.Status;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long>, QueryDslMemberRepository {

    Optional<Member> findByEmail(String email);
    boolean existsMemberByEmail(String email);
}
