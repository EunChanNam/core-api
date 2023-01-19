package com.learcha.learchaapp.auth.repository;

import com.learcha.learchaapp.auth.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsMemberByEmail(String email);
}
