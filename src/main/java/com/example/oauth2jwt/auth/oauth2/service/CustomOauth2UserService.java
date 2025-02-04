package com.example.oauth2jwt.auth.oauth2.service;



import com.example.oauth2jwt.auth.oauth2.oauth2userinfo.Oauth2UserInfo;
import com.example.oauth2jwt.member.domain.Member;
import com.example.oauth2jwt.member.domain.RoleType;
import com.example.oauth2jwt.member.repository.MemberRepository;
import com.example.oauth2jwt.auth.oauth2.domain.SocialType;
import com.example.oauth2jwt.auth.oauth2.dto.CustomOauth2User;
import com.example.oauth2jwt.auth.oauth2.oauth2userinfo.Oauth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/*
- OAuth2 로직 -
1. 일단 OAuth2에서 책임분배는 프론트에서 다 맡거나 백엔드에서 다 맡는것이 좋다. 이 프젝에서는 백에서 다 구현.
큰 로직 -
- 클라이언트에서 소셜 로그인 버튼을 누르면 서버에 로그인 주소를 요청한다. http://localhost:8080/oauth2/authorization/google
- 백엔드에서는 소셜 로그인 페이지 주소를 반환 후 사용자에게 소셜 로그인 페이지를 보여준다. 이때 로그인 후 redirect 주소를 백엔드로 설정.
    - http://localhost:8080/login/oauth2/code/google
- 로그인 진행 후 인증 서버에서 발급한 코드와 함께 설정한 백엔드 주소로 리다이렉팅 된다.
- 백엔드에서는 인가 코드로 소셜 로그인 인증 서버에 엑세스 토큰을 요청한다.
- 백엔드에서 인증 서버로 부터 받은 엑세스 토큰으로 리소스 서버에 사용자 정보를 요청한다.
========== 여기까지 스프링 시큐리티가 알아서 진행시켜 준다. ==============
- 리소스 서버로부터 받은 사용자 정보를 DB에 저장 후 서비스 내에 엑세스 토큰과 리프레쉬 토큰 발급.
- 클라이언트에 전달.

// OAuth2 관련 필터는 소셜 로그인을 진행할 떄만 작동한다.
// OAuth2 로그인을 사용한다면 시큐리티 필터 중 UsernamePasswordAuthenticationFilter 대신 OAuth2LoginAuthenticationFilter 호출됨.

 */

   /* OAuth2 filter 로직 - 세부적인거는 직접 코드를 찾아보면서 이해하기. 큰 틀만 작성.
// OAuth2AuthorizationRequestRedirectFilter - 프론트가 백엔드로 로그인 주소를 요청하고 로그인 페이지 url을 반환하는 로직을 수행하는 필터이다.
     ->  /oauth2/authorization/naver 같은 URL로 요청을 보내면 OAuth2AuthorizationRequestRedirectFilter가 가로채고
     ->  요청 URL과 파라미터를 검증을 한 후 조건이 일치하면 다음 작업을 진행한다.
// OAuth2LoginAuthenticationFilter - 사용자가 로그인 후 인증 서버에서 인가 코드를 백엔드 서버로 리다이렉트하는데 해당 요청을 처리하고 엑세스 토큰 요청하고 사용자 정보를 받아오는 필터
    -> 실제로는 doFilter가 OAuth2LoginAuthenticationFilter의 정의 되어 있지 않아  상위 클래스 AbstractAuthenticationProcessingFilter를 호출한다.
// AbstractAuthenticationProcessingFilter는 먼저 요청 URL이 /login/oauth2/code/* 패턴과 맞는 지 확인한다.
    -> 일치하면 인증을 하기 위해 attemptAuthentication를 호출하는데 해당 메서드는 추상 메서드이고 하위 클래스인 OAuth2LoginAuthenticationFilter에서 구현함.
    -> 인증 성공  시 successfulAuthentication 메서드에서 스프링 시큐리티 컨텍스트에 인증 객체를 저장하고, successHandler 를 호출힌다.
// CustomOAuth2UserService - loadUser()로 리소스 서버에서 사용자 정보를 가져와 유저 정보를 가공 후 OAuth2User 구현체로 리턴
    -> OAuth2LoginAuthenticationProvider가 loadUser를 호출한다.
    -> CustomOAuth2UserService를 config에 등록하면 로그인 성공 후 해당 클래스를 호출.
// OAuth2AuthenticationSuccessHandler - OAuth2 인증이 성공적으로 끝나고 DB 저장하고 서비스 내 자체 토큰 발급
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // userRequest - 사용자 인증 요청에 대한 정보를 담고 있는 객체
        OAuth2User oAuth2User = super.loadUser(userRequest); // 상위 클래스의 loadUser()로 uesrRequest를 넘겨 리소스 서버에서 사용자 정보를 가져온다.
        try {
            return this.processOAuth2User(userRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }

    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

         // registrationId으로 SocialType 저장.
       SocialType socialType = getSocialType(userRequest.getClientRegistration().getRegistrationId());
        Oauth2UserInfo userInfo = Oauth2UserInfoFactory.getOAuth2UserInfo(socialType, oAuth2User.getAttributes());// 소셜 종류에 따라 정보 가져옴
        Member member = memberRepository.findBySocialLoginId(
                userInfo.getProviderId()
        ).orElse(null);

        if (member != null) {  // 로그인 여부 체크
            updateMember(member,userInfo);
        }else {
            member = createMember(userInfo, socialType);
//            memberRepository.flush();  // 즉시 저장 반영
        }
        return new CustomOauth2User(member,oAuth2User.getAttributes());
    }

    // 기존에 있던 회원이더라도 이메일이나 닉네임이 변경되었을 수도 있으니 업데이트
    private Member updateMember(Member member, Oauth2UserInfo userInfo) {
        member.updateNickname(userInfo.getName());
        member.updateUsername(userInfo.getEmail());
        return member;
    }

    private Member createMember(Oauth2UserInfo userInfo,SocialType socialType) {

        Member member = Member.builder()
                .nickname(userInfo.getName())
                .username(userInfo.getEmail())
                .roleType(RoleType.MEMBER)
                .socialLoginId(userInfo.getProviderId())
                .socialType(socialType)
                .build();
        log.info("{}", member);
        return memberRepository.save(member);
    }

    private SocialType getSocialType(String registrationId) {
        if ("naver".equalsIgnoreCase(registrationId)) {
            return SocialType.NAVER;
        }
        if ("kakao".equalsIgnoreCase(registrationId)) {
            return SocialType.KAKAO;
        }
        return SocialType.GOOGLE;
    }

}
