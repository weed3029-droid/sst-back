package sst.member.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
	private Long memberId; 
	private String memberEmail; 
	private String memberPassword; 
	private String memberName; 
	private String memberNickname; 
	private String memberRole; 
	private String memberPhone; 
	private String memberStatus; 
	private LocalDateTime memberCreatedAt; 
	private LocalDateTime memberUpdatedAt; 
	private String memberRefreshToken;
}


