package sst.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class MemberUpdateRequest {
    @NotBlank(message = "이름은 필수입니다.")
    private String mbrName;
    
    @NotBlank(message = "닉네임은 필수입니다.")
    private String mbrNickname;
    
    @NotBlank(message = "전화번호는 필수입니다.")
    private String mbrTelno;
    
    private String mbrZip;
    private String mbrAddr;
    private String mbrDaddr;
}