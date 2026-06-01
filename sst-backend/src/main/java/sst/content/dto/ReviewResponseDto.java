package sst.content.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {

    private Long          rvwNo;
    private Long          rvwPlcNo;
    private Long          rvwMbrId;
    private Double        rvwRating;
    private String        rvwContent;
    private LocalDateTime rvwRegDate;
    private LocalDateTime rvwUpDate;
    private String        nickname;

    // 캐시 동기화 결과
    private Double  avgRating;
    private Integer reviewCount;
    
    private String rvwUseYn; // 상태 (Y/N)
    private String plcName;  // 조인용 장소명
    private Integer plcRgnCd; // 장소 지역 코드
    private String plcCatCd; // 장소 카테고리 코드
}