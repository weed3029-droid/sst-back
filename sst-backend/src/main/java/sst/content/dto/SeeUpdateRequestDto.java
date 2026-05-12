package sst.content.dto;

import lombok.Data;

@Data
public class SeeUpdateRequestDto {
    // 🚀 PLACE 테이블 공통 컬럼
    private String plcName;
    private String plcAddr;
    
    // 🚀 PLACE_SEE 테이블 상세 컬럼
    private String seeInfocenter;
    private String seeParking;
    private String seeRestdate;
    private String seeUsetime;
}