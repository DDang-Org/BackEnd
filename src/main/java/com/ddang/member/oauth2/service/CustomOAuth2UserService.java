package com.ddang.member.oauth2.service;

import com.ddang.member.entity.Member;
import com.ddang.member.entity.Provider;
import com.ddang.member.entity.Role;
import com.ddang.member.oauth2.CustomOAuth2User;
import com.ddang.member.oauth2.OAuth2Attributes;
import com.ddang.member.oauth2.userinfo.OAuth2UserInfo;
import com.ddang.member.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.ddang.member.oauth2.OAuth2Attributes.getProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);


        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Provider provider = getProvider(registrationId);

        log.info("registrationId={}", registrationId);
        log.info("userNameAttributeName={}", userNameAttributeName);
        log.info("provider={}", provider);


        if(registrationId.contains("apple")){
            Map<String, Object> attributes;
            Map<String, Object> userAttributes = new HashMap<>();

            String idToken = userRequest.getAdditionalParameters().get("id_token").toString();
            attributes = decodeJwtTokenPayload(idToken);
            attributes.put("id_token", idToken);
            userAttributes.put("resultcode", "00");
            userAttributes.put("message", "success");
            userAttributes.put("response", attributes);

            OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(provider, userNameAttributeName, userAttributes);
            OAuth2UserInfo oauth2UserInfo = oAuth2Attributes.getOauth2UserInfo();
            String email = oauth2UserInfo.getEmail();

            Member member = memberRepository.findByEmail(email)
                    .orElse(Member.builder().email(email).role(Role.GUEST).provider(provider).build());

            return new CustomOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())),
                    attributes,
                    oAuth2Attributes.getNameAttributeKey(),
                    member
            );
        }

        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(provider, userNameAttributeName, attributes);

        OAuth2UserInfo oauth2UserInfo = oAuth2Attributes.getOauth2UserInfo();
        String email = oauth2UserInfo.getEmail();

        // 소셜 타입과 소셜 ID 로 조회된다면 이전에 로그인을 한 유저
        // DB 에 조회되지 않는다면 Role 을 GUEST 로 설정하여 반환 -> LoginSuccessHandler 에서 회원가입으로 리다이렉트 후 추가 정보를 받는다
        Member member = memberRepository.findByEmail(email)
                .orElse(Member.builder().email(email).role(Role.GUEST).provider(provider).build());

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())),
                attributes,
                oAuth2Attributes.getNameAttributeKey(),
                member
        );
    }

    public Map<String, Object> decodeJwtTokenPayload(String jwtToken) {
        Map<String, Object> jwtClaims = new HashMap<>();

        try {
            String[] parts = jwtToken.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();

            byte[] decodedBytes = decoder.decode(parts[1].getBytes(StandardCharsets.UTF_8));
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> map = mapper.readValue(decodedString, Map.class);
            jwtClaims.putAll(map);

        } catch (JsonProcessingException e) {
            log.error("decodeJwtToken: {}-{} / jwtToken : {}", e.getMessage(), e.getCause(), jwtToken);
        }
        return jwtClaims;
    }

}
