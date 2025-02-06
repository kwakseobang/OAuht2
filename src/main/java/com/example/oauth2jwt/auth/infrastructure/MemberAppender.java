package com.example.oauth2jwt.auth.infrastructure;


import com.example.oauth2jwt.auth.oauth2.domain.SocialType;
import com.example.oauth2jwt.global.error.ErrorCode;
import com.example.oauth2jwt.global.exception.CustomException;
import com.example.oauth2jwt.member.domain.Member;
import com.example.oauth2jwt.member.domain.RoleType;
import com.example.oauth2jwt.member.infrastructure.MemberValidator;
import com.example.oauth2jwt.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAppender {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;
    private final MemberValidator memberValidator;
    public void signUp(String username,String password,String nickname) {
        if (memberValidator.isExistUsername(username)) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME,String.format("존재하는 username: %s ",username));
        }
        if (memberValidator.isExistNickname(nickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME,String.format("존재하는 nickname: %s ",nickname));
        }
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        Member member = Member.SaveBuilder()
                .username(username)
                .password(encodedPassword)
                .nickname(nickname)
                .roleType(RoleType.MEMBER)
                .socialType(SocialType.LOCAL)
                .build();
        memberRepository.save(member);
    }
}
