package com.example.oauth2jwt.member.infrastructure;


import com.example.oauth2jwt.global.error.ErrorCode;
import com.example.oauth2jwt.global.exception.CustomException;
import com.example.oauth2jwt.member.domain.Member;
import com.example.oauth2jwt.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberValidator {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean isExistUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    public boolean isExistNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public Member validateLogin(String username, String password) {
        // 유효성 검사.
        Member member = validateMember(username);
        validatePassword(password,member.getPassword());
        return member;
    }

    private Member validateMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NOT_FOUND_MEMBER,
                        String.format("username: %s", username)
                ));
    }

    private void validatePassword(String rawPassword, String encryptedPassword) {
        // mathes(평문 패스워드, 암호화 패스워드) 순서로 해야 됨.
        if (!bCryptPasswordEncoder.matches(rawPassword, encryptedPassword)) {
            throw new CustomException(ErrorCode.BAD_REQUEST_PASSWORD);
        }
    }
}
