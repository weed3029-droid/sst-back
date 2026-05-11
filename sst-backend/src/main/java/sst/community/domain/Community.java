package sst.community.domain;

import lombok.Data;

@Data
public class Community {

    private Long commNo;
    private Long commMbrId;
    private String mbrNickname;

    private String commCatCd;
    private Long commAisNo;

    private String commMainImgUrl;
    private String commTitle;
    private String commContent;

    private int commInqireCnt;
    private int commLikeCnt;
    private int commCmntCnt;

    private String commRegDate;
    private String commUpDate;
    private String commUseYn;
}