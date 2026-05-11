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

    public List<Community> getCommunityList(String catCd) {
        return communityMapper.selectCommunityList(catCd);
    }
    
    public Community getCommunityDetail(Long commNo) {
        return communityMapper.selectCommunityDetail(commNo);
    }
}