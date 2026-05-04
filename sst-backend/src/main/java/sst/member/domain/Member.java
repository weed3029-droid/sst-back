package sst.member.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
	private Long mbrId;                  
    private String mbrPassword;          
    private String mbrName;              
    private String mbrNickname;          
    private String mbrEmail;             
    private String mbrTelno;             
    private String mbrZip;               
    private String mbrAddr;              
    private String mbrDaddr;             
    private String mbrProviderCd;        
    private String mbrAuthCd;            
    private String mbrRefreshToken;      
    private LocalDateTime mbrJoinDate;   
    private LocalDateTime mbrLastLgnDate;
    private String mbrUseYn;
    
}


