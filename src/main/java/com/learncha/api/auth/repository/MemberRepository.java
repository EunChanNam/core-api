package com.learncha.api.auth.repository;

import com.learncha.api.auth.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsMemberByEmail(String email);
    Optional<Member> findByEmailAndAuthenticationCode(String email, String authenticationCode);
}
