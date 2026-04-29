package sst.auth.service;

import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import sst.auth.dto.LoginRequest;
import sst.auth.dto.LoginResponse;
import sst.auth.dto.SignUpRequest;
import sst.global.exception.CustomException;
import sst.global.exception.ErrorCode;
import sst.global.security.provider.JwtTokenProvider;
import sst.global.utils.CookieUtil;
import sst.member.domain.Member;
import sst.member.mapper.MemberMapper;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final PasswordEncoder passwordEncoder;
	private final MemberMapper memberMapper;
	private final JwtTokenProvider jwtTokenProvider;
	private final CookieUtil cookieUtil;
	
	/**
	 * 회원 가입
	 * @param SignUpRequest 사용자 가입정보 
	 */
	@Transactional
	public Member addMember(SignUpRequest request) {
		// 이메일 중복 체크
		memberMapper.findMemberByEmail(request.getMemberEmail())
					.ifPresent(member -> {
						throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
					});
		
		Member member = Member.builder()
							  .memberEmail(request.getMemberEmail())
							  .memberPassword(passwordEncoder.encode(request.getMemberPassword())) // 패스워드 암호화
							  .memberName(request.getMemberName())
							  .memberNickname(request.getMemberNickname())
							  .memberPhone(request.getMemberPhone())
							  .memberRole("ROLE_USER")
							  .memberStatus("1")
							  .build();
			  
		memberMapper.saveMember(member);
		
		return member;
	}
	
	/**
	 * 로그인
	 * @param LoginRequest 로그인 정보
	 */
	@Transactional
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {

        // 회원 정보 조회
        Member member = memberMapper.findMemberByEmail(request.getMemberEmail())
        							.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 비밀번호 검증 (입력값 평문 vs DB 암호화값 비교)
        if (!passwordEncoder.matches(request.getMemberPassword(), member.getMemberPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        // Access Token + Refresh Token 발급 
        String accessToken  = jwtTokenProvider.createAccessToken(member.getMemberEmail(), member.getMemberRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberEmail());

        // Refresh Token을 DB에 저장 
        memberMapper.updateRefreshTokenById(member.getMemberId(), refreshToken);

        // httpOnly 쿠키로 토큰을 브라우저에 전달
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtil.createAccessTokenCookie(accessToken).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtil.createRefreshTokenCookie(refreshToken).toString());
        
        return LoginResponse.builder()
        					.memberId(member.getMemberId())
        					.memberEmail(member.getMemberEmail())
        					.memberName(member.getMemberName())
        					.memberNickname(member.getMemberNickname())
        					.memberRole(member.getMemberRole())
        					.build();
    }
	
	/**
     * Access Token 재발급 (Refresh Token 검증 후)
     * @param request  Refresh Token 쿠키를 포함한 요청
     * @param response 새 Access Token 쿠키를 추가할 응답
     */
    public void refresh(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 Refresh Token 추출
        String refreshToken = cookieUtil.extractCookie(request, CookieUtil.REFRESH_TOKEN_COOKIE);
        if (refreshToken == null) {
        	throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // JWT 파싱으로 이메일 추출 (만료 시 ExpiredTokenException 발생)
        String email = jwtTokenProvider.getEmail(refreshToken);

        // DB에 저장된 RefreshToken과 비교 (탈취된 토큰 재사용 방지)
        Member member = memberMapper.findMemberByEmail(email)
        							.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        if (!refreshToken.equals(member.getMemberRefreshToken())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 새 Access Token 발급 → 쿠키로 전달
        String newAccessToken = jwtTokenProvider.createAccessToken(email, member.getMemberRole());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtil.createAccessTokenCookie(newAccessToken).toString());
    }
    
    /**
     * 로그아웃 — DB의 Refresh Token 삭제 + 쿠키 만료
     * @param email    로그아웃할 회원 이메일 (@AuthenticationPrincipal에서 추출)
     * @param id       로그아웃할 회원 ID
     * @param response 쿠키 삭제를 위한 응답 객체
     */
    @Transactional
    public void logout(Long memberId, HttpServletResponse response) {

        // DB에서 Refresh Token null로 업데이트 (무효화)
        memberMapper.updateRefreshTokenById(memberId, null);

        // Access Token/Refresh Token 쿠키 삭제
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtil.deleteAccessTokenCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtil.deleteRefreshTokenCookie().toString());

    }
}






