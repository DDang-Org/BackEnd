package com.ddang.member.oauth2;

import com.ddang.member.entity.Member;
import com.ddang.member.entity.Provider;
import com.ddang.member.entity.Role;
import com.ddang.member.oauth2.userinfo.AppleOAuth2UserInfo;
import com.ddang.member.oauth2.userinfo.GoogleOAuth2UserInfo;
import com.ddang.member.oauth2.userinfo.KakaoOAuth2UserInfo;
import com.ddang.member.oauth2.userinfo.OAuth2UserInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuth2Attributes {

    private String nameAttributeKey; 
    private OAuth2UserInfo oauth2UserInfo;

    @Builder
    private OAuth2Attributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    public static OAuth2Attributes of(Provider provider,
                                      String userNameAttributeName, Map<String, Object> attributes) {

        if (provider == Provider.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }

        if(provider == Provider.APPLE) {
            return ofApple(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuth2Attributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuth2Attributes ofApple(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new AppleOAuth2UserInfo(attributes))
                .build();
    }

    public Member toEntity(OAuth2UserInfo oauth2UserInfo, Provider provider) {
        return Member.builder()
                .email(oauth2UserInfo.getEmail())
                .role(Role.GUEST)
                .provider(provider)
                .build();
    }

    public static Provider getProvider(String registrationId) {
        // Member 에서도 사용하기 위해 대문자로 변경
        if (registrationId != null) {
            registrationId = registrationId.toUpperCase();
        }

        if ("GOOGLE".equals(registrationId)) {
            return Provider.GOOGLE;
        } else if ("KAKAO".equals(registrationId)) {
            return Provider.KAKAO;
        } else if ("APPLE".equals(registrationId)){
            return Provider.APPLE;
        }
        return null;
    }

}

