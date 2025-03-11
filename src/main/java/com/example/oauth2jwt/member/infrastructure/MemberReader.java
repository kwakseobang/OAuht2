package com.example.oauth2jwt.member.infrastructure;


import com.example.oauth2jwt.global.response.error.ErrorCode;
import com.example.oauth2jwt.member.domain.Member;
import com.example.oauth2jwt.member.exception.MemberException;
import com.example.oauth2jwt.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberReader {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Member login(String username, String password) {
        Member member = getMember(username);
        validatePassword(password, member.getPassword());
        return member;
    }

    private Member getMember(String username) {
        return memberRepository.findByUsername(username)
            .orElseThrow(() -> new MemberException(
                ErrorCode.NOT_FOUND_MEMBER,
                String.format("username: %s", username)
            ));
    }

    private void validatePassword(String rawPassword, String encryptedPassword) {
        // matches(평문 패스워드, 암호화 패스워드) 순서로 해야 됨.
        if (!bCryptPasswordEncoder.matches(rawPassword, encryptedPassword)) {
            throw new MemberException(ErrorCode.BAD_REQUEST_PASSWORD);
        }
    }

}