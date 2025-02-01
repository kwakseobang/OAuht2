package com.example.oauth2jwt.service;

import com.example.oauth2jwt.dto.CustomOAuth2User;
import com.example.oauth2jwt.dto.GoogleResponse;
import com.example.oauth2jwt.dto.NaverResponse;
import com.example.oauth2jwt.dto.OAuth2Response;
import com.example.oauth2jwt.dto.UserDTO;
import  com.example.oauth2jwt.domain.User;
import com.example.oauth2jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 어느 서비스에서 온 값인지 확인 naver, google 등..
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {

            return null;
        }
        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        //추후 작성

        User existData = userRepository.findByUsername(username);

        if (existData == null) { // 로그인 경험 X

        User user = User.builder()
                .username(username)
                .email(oAuth2Response.getEmail())
                .name(oAuth2Response.getName())
                .role("ROLE_USER")
                .build();

            userRepository.save(user);

            UserDTO userDTO = UserDTO.builder()
                    .username(username)
                    .name(oAuth2Response.getName())
                    .role("ROLE_USER")
                    .build();


            return new CustomOAuth2User(userDTO);
        } else { // 로그인 경험 O

            existData.updateEmail(oAuth2Response.getEmail());
            existData.updateName(oAuth2Response.getName());

            userRepository.save(existData);

            UserDTO userDTO = UserDTO.builder()
                    .username(existData.getUsername())
                    .name(oAuth2Response.getName())
                    .role(existData.getRole())
                    .build();

            return new CustomOAuth2User(userDTO);
        }
    }
}
