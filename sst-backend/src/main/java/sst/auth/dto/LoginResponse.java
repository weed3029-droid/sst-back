package sst.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sst.global.files.domain.FileDomain;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

	private Long mbrId;
    private String mbrEmail;
    private String mbrName;
    private String mbrNickname;
    private String memberRole;
    private String mbrProviderCd;
    private String mbrProfileFileNo; 	// 프로필 아이디 번호
    private FileDomain mbrProfileIcon;	// 프로필 아이콘 파일 정보
    private FileDomain mbrProfileBg;	// 프로필 배경 파일 정보
}
