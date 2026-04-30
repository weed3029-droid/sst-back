package sst.global.security.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sst.global.security.domain.CustomUserDetails;
import sst.member.domain.Member;
import sst.member.mapper.MemberMapper;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 카카오에서 응답받은 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");

        // 기존 MemberMapper를 활용하여 이메일로 회원 조회
        Member member = memberMapper.findMemberByEmail(email).orElse(null);

        if (member == null) {
            // 미가입자라면 임의의 비밀번호를 생성하여 즉시 회원가입 처리
            String dummyPassword = passwordEncoder.encode(UUID.randomUUID().toString());
            
            member = Member.builder()
                    .memberEmail(email)
                    .memberPassword(dummyPassword) 
                    .memberName(nickname)
                    .memberNickname(nickname)
                    .memberRole("ROLE_USER")
                    .memberPhone("010-0000-0000") // 카카오에서 번호를 받지 않을 경우 임시값
                    .memberStatus("1")
                    .build();
            
            memberMapper.saveMember(member); //
        }

        // 기존에 만들어둔 CustomUserDetails 재활용
        return new CustomUserDetails(member, attributes, "id");
    }
}