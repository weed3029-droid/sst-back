package sst.member.dto;

import lombok.Data;

@Data
public class MemberStatusLogDto {
    private String statusVal; // Y, N, W
    private String reason;    // 사유
    private String adminName; // 처리한 관리자 닉네임
    private String regDate;   // 처리 일시
}