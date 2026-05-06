package sst.member.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sst.member.domain.Member;

@Mapper
public interface MemberMapper {
	// 회원등록
	int saveMember(Member member);
	
	// 회원 정보 조회 (이메일)
	Optional<Member> findMemberByEmail(@Param(value = "email") String email);
	
	// 리프레시토큰 업데이트
	int updateRefreshTokenById(@Param("mbrId") Long memberId,
			   				   @Param("refreshToken") String refreshToken);
	
	// 닉네임 중복환인
	int existsByNickname(@Param("nickname") String nickname);
	
	// 회원 정보 수정 
    int updateMemberInfo(Member member);
    
    // 이메일 중복 조회
    int existsByEmail(@Param("email") String email);
    
    // 마지막 로그인 시간 업데이트
    int updateLastLoginDate(@Param("mbrId") Long memberId);
}
