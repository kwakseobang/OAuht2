package com.example.oauth2jwt.member.infrastructure;


import com.example.oauth2jwt.global.error.ErrorCode;
import com.example.oauth2jwt.global.exception.CustomException;
import com.example.oauth2jwt.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberManager {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public void isExistUsername(String username) {
        if (memberRepository.existsByUsername(username))  {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME,String.format("존재하는 username: %s ",username));
        }
    }

    @Transactional(readOnly = true)
    public void isExistNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname))  {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME,String.format("존재하는 nickname: %s ",nickname));
        }
    }
}
