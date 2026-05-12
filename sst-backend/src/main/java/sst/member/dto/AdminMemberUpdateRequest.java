package sst.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminMemberUpdateRequest {
    // 🚀 관리자가 기존 회원을 수정할 때 사용하는 DTO (이메일 수정 불가)
    private String mbrPassword; // 비워두면 변경하지 않음
    private String mbrName;
    private String mbrNickname;
    private String mbrTelno;
    private String mbrZip;
    private String mbrAddr;
    private String mbrDaddr;
    
    private String mbrAuthCd;
    private String mbrUseYn;
}