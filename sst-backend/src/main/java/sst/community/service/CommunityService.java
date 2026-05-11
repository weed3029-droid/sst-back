package sst.community.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sst.community.domain.Community;
import sst.community.mapper.CommunityMapper;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityMapper communityMapper;

    // 커뮤니티 게시글 목록 조회
    public List<Community> getCommunityList(String catCd) {
        return communityMapper.selectCommunityList(catCd);
    }
    
    // 커뮤니티 게시글 상세 조회
    public Community getCommunityDetail(Long commNo) {
        return communityMapper.selectCommunityDetail(commNo);
    }
    
    // 커뮤니티 게시글 등록
    public void createCommunity(Community community) {
        communityMapper.insertCommunity(community); // 실제 DB INSERT 실행
    }
    
    // 커뮤니티 게시글 수정
    public void modifyCommunity(Community community) {
        communityMapper.updateCommunity(community);
    }
    
    // 커뮤니티 게시글 삭제
    public void removeCommunity(Long commNo) {
        communityMapper.deleteCommunity(commNo);
    }
    
}