package com.learncha.api.auth.repository;

import com.learncha.api.auth.domain.Member;
import com.learncha.api.auth.domain.Member.Status;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByEmailAndStatusIsNot(String email, Status status);
    boolean existsMemberByEmail(String email);
}
