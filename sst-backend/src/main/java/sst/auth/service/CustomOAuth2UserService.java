
package sst.auth.service;

import java.util.Map;
import java.util.UUID;
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

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 🚀 카카오는 응답 JSON 구조가 깊어서 (kakao_account 안의 profile 등) 꺼내서 써야 함
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");

        // 🚀 DB에서 이메일로 유저 조회, 없으면 강제 회원가입 (소셜 로그인 최초 1회)
        Member member = memberMapper.findMemberByEmail(email)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .memberEmail(email)
                            // 🚀 소셜 유저는 비밀번호를 안 쓰므로 임의의 UUID 저장
                            .memberPassword(UUID.randomUUID().toString()) 
                            .memberName(nickname)
                            .memberNickname(nickname)
                            .memberRole("ROLE_USER")
                            .memberPhone("010-0000-0000") // 소셜 연동 시 번호를 못 받으면 임시값 처리
                            .memberStatus("1")
                            .build();
                    memberMapper.saveMember(newMember);
                    return newMember; // 저장 직후의 Member 객체 반환 (keyProperty로 ID가 담김)
                });

        // 🚀 기존에 만들어둔 CustomUserDetails 재활용 (OAuth2User 구현체 역할)
        return new CustomUserDetails(member, attributes, "id");
    }
}