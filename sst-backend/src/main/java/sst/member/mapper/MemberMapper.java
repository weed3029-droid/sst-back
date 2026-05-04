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
}
