package com.learcha.learchaapp.member.repository;

import com.learcha.learchaapp.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
