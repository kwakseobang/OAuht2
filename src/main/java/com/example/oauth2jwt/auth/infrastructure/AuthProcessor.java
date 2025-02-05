package com.example.oauth2jwt.auth.infrastructure;


import com.example.oauth2jwt.member.domain.Member;
import com.example.oauth2jwt.member.domain.RoleType;
import com.example.oauth2jwt.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthProcessor {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;
    @Transactional
    public void signUp(String username,String password,String nickname) {
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        Member member = Member.builder()
                .username(username)
                .password(encodedPassword)
                .nickname(nickname)
                .roleType(RoleType.MEMBER)
                .build();
        memberRepository.save(member);
    }
}
