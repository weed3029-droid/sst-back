package sst.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawalRequest {
    // 🚀 탈퇴 사유 코드 (WDR001 ~ WDR004)
    private String reasonCd;
    // 🚀 '기타' 선택 시 작성할 상세 사유 (없으면 null)
    private String reasonDesc; 
}