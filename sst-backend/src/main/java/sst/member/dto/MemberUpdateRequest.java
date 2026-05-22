package sst.member.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 회원 Form Request Data 받아오기 위한 DTO 클래스 
 * 
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MemberUpdateRequest {
	
    @NotBlank(message = "이름은 필수입니다.")
    private String mbrName;
    
    // 회원 이름
    @NotBlank(message = "닉네임은 필수입니다.")
    private String mbrNickname;
    
    @NotBlank(message = "전화번호는 필수입니다.")
    private String mbrTelno;
    
    private String mbrZip;
    private String mbrAddr;
    private String mbrDaddr;
    
    // 프로필 이미지 파일을 받기 위한 필드
    private MultipartFile profileImage;
    
    // 나중에 추가할 배경 이미지를 위해 미리 만들어둬도 좋습니다.
    private MultipartFile backgroundImage;
    
}