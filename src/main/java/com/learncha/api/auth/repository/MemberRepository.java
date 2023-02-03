package com.learncha.api.auth.repository;

import com.learncha.api.auth.domain.Member;
import com.learncha.api.auth.domain.Member.Status;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    Optional<Member> findByEmailAndStatusIsNot(String email, Status status);
    @Query(
        value = "select * from member as m where m.email = email and m.status != 'Deleted'",
        nativeQuery = true
    )
    Optional<Member> findByEmailAndStatusIsNotDeleted(String email);
    boolean existsMemberByEmail(String email);

    boolean findByPasswordAndStatusIsNot(String password, Status status);
}
