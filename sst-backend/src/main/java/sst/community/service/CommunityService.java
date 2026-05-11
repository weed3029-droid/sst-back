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
        if (community.getHashtags() == null) {
            return;
            }
        for (String tagName : community.getHashtags()) {
            Long tagNo = communityMapper.selectHashtagNo(tagName);
            if (tagNo == null) {
                communityMapper.insertHashtag(tagName);
                tagNo = communityMapper.selectHashtagNo(tagName);
            }
            communityMapper.insertCommunityHashtag(community.getCommNo(), tagNo);
        }
    }
    
    // 커뮤니티 게시글 수정
    public void modifyCommunity(Community community) {

        // 게시글 제목/내용/대표이미지 수정
        communityMapper.updateCommunity(community);

        // 기존 해시태그 연결 전체 삭제
        communityMapper.deleteCommunityHashtags(community.getCommNo());

        // 해시태그가 없으면 종료
        if (community.getHashtags() == null) {
            return;
        }

        // 새 해시태그 다시 연결
        for (String tagName : community.getHashtags()) {

            Long tagNo = communityMapper.selectHashtagNo(tagName);

            if (tagNo == null) {
                communityMapper.insertHashtag(tagName);
                tagNo = communityMapper.selectHashtagNo(tagName);
            }

            communityMapper.insertCommunityHashtag(community.getCommNo(), tagNo);
        }
    }
    
    // 커뮤니티 게시글 삭제
    public void removeCommunity(Long commNo) {
        communityMapper.deleteCommunity(commNo);
    }
    
    // 커뮤니티 게시글 조회수 증가
    public void increaseViewCount(Long commNo) {
        communityMapper.updateViewCount(commNo);
    }
    
    // 커뮤니티 게시글 좋아요 처리
    public boolean toggleLike(Long commNo, Long mbrId) {
        // 이미 좋아요 눌렀는지 확인
        int likeCount = communityMapper.selectLikeCount(commNo, mbrId);
        // 좋아요 이미 눌렀으면 취소
        if (likeCount > 0) {
            communityMapper.deleteCommunityLike(commNo, mbrId);
            // 게시글 좋아요 수 감소
            communityMapper.decreaseLikeCount(commNo);
            return false;
        }
        // 좋아요 안 눌렀으면 추가
        communityMapper.insertCommunityLike(commNo, mbrId);
        // 게시글 좋아요 수 증가
        communityMapper.increaseLikeCount(commNo);
        return true;
    }
    
}