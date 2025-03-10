package com.example.oauth2jwt.member.infrastructure;


import com.example.oauth2jwt.global.response.error.ErrorCode;
import com.example.oauth2jwt.member.domain.Member;
import com.example.oauth2jwt.member.domain.RoleType;
import com.example.oauth2jwt.member.exception.MemberException;
import com.example.oauth2jwt.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberAppender {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;

    public void signUp(String username, String password, String nickname) {
        validateUsername(username);
        validateNickname(nickname);

        Member member = Member.SaveBuilder()
            .username(username)
            .password(bCryptPasswordEncoder.encode(password))
            .nickname(nickname)
            .roleType(RoleType.MEMBER)
            .build();

        memberRepository.save(member);
    }

    private void validateUsername(String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new MemberException(
                ErrorCode.DUPLICATE_USERNAME,
                String.format("존재하는 username: %s ", username)
            );
        }
    }
    private void validateNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new MemberException(
                ErrorCode.DUPLICATE_NICKNAME,
                String.format("존재하는 nickname: %s ", nickname)
            );
        }
    }

}