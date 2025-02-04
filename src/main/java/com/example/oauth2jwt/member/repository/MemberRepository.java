package com.example.oauth2jwt.member.repository;

import com.example.oauth2jwt.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
     Optional<Member> findBySocialLoginId(String socialLoginId);

}
