package sst.plan.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlaceResponseDto {

    private Long         id;                // PLC_NO
    private String       name;              // PLC_NAME
    private Integer      regionCode;        // PLC_RGN_CD
    private String       regionName;        // RGN_NAME
    private String       placeCategory;     // PLC_CAT_CD
    private String       placeCategoryName; // CMM_CD_NAME (카테고리)
    private String       x;                 // PLC_LOT
    private String       y;                 // PLC_LAT
    private String       overview;          // PLC_OVERVIEW
    private List<String> placeTheme;        // PTAG_TAG_CD 목록
    private List<String> placeThemeName;    // CMM_CD_NAME (테마) 목록
    private String       placeFilter;       // PLC_FLT_CD
    private String       placeFilterName;   // CMM_CD_NAME (필터)
}