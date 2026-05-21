package sst.community.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import sst.community.domain.Community;
import sst.community.dto.CommunityDto;
import sst.community.dto.CommunityFileMapDto;
import sst.community.dto.PlaceCategoryDto;
import sst.community.dto.PlaceDto;
import sst.community.dto.RegionDto;
import sst.community.mapper.CommunityMapper;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import sst.global.files.domain.FileDomain;
import sst.global.files.dto.FileUploadResult;
import sst.global.files.mapper.FileMapper;
import sst.global.files.storage.FileStorage;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityMapper communityMapper;
    
    private final FileMapper fileMapper;
	private final FileStorage fileStorage;

    // 커뮤니티 게시글 목록 조회
    public PageResponse<Community> getCommunityList(
            String catCd,
            PageRequest pageRequest
    ) {
        List<Community> list = communityMapper.selectCommunityList(
                catCd,
                pageRequest.getSearchType(),
                pageRequest.getKeyword(),
                pageRequest.getSortType(),
                pageRequest.getOffset(),
                pageRequest.getSize()
        );

        int totalCount = communityMapper.countCommunityList(
                catCd,
                pageRequest.getSearchType(),
                pageRequest.getKeyword()
        );

        return new PageResponse<>(list, totalCount, pageRequest);
    }
    
    // 커뮤니티 게시글 상세 조회
    public Community getCommunityDetail(Long commNo) {
        Community community = communityMapper.selectCommunityDetail(commNo);

        if (community != null) {
            List<String> images = communityMapper.selectCommunityImages(commNo);
            community.setImages(images);
        }

        return community;
    }
    
    // 커뮤니티 게시글 등록
    @Transactional
    public void createCommunity(CommunityDto communityDto, List<MultipartFile> files) {

        // 핫플거리는 이미지 필수
        if ("CMM002".equals(communityDto.getCommCatCd())
                && (files == null || files.isEmpty())) {
            throw new IllegalArgumentException("핫플거리는 사진을 1장 이상 등록해야 합니다.");
        }
        
        if (files == null) {
            files = List.of();
        }

        fileStorage.setup("community")
                .subPath(communityDto.getCommCatCd())
                .allow(List.of("jpg", "jpeg", "png", "gif", "webp"))
                .maxSize("10MB")
                .onSuccess(context -> {

                    List<FileUploadResult> savedFiles = context.getSavedFiles();

                    // 대표 이미지 세팅
                    if (savedFiles != null && !savedFiles.isEmpty()) {
                        communityDto.setCommMainImgUrl(savedFiles.get(0).getFilePath());
                    }

                    // 게시글 등록
                    communityMapper.insertCommunity(communityDto);

                    // 해시태그 등록
                    insertHashtags(communityDto);

                    // FILE 테이블 + COMMUNITY_FILE_MAP 등록
                    if (savedFiles != null && !savedFiles.isEmpty()) {
                        for (int i = 0; i < savedFiles.size(); i++) {
                            FileUploadResult saved = savedFiles.get(i);

                            FileDomain fileDomain = FileDomain.builder()
                                    .fileOrgNm(saved.getFileOrgNm())
                                    .fileSaveNm(saved.getFileSaveNm())
                                    .filePath(saved.getFilePath())
                                    .fileExt(saved.getFileExt())
                                    .fileSize(saved.getFileSize())
                                    .fileMimeType(saved.getContentType())
                                    .fileType("IMAGE")
                                    .build();

                            fileMapper.insertFile(fileDomain);

                            CommunityFileMapDto map = new CommunityFileMapDto();
                            map.setCommNo(communityDto.getCommNo());
                            map.setFileNo(fileDomain.getFileNo());
                            map.setSortOrdr(i);

                            communityMapper.insertCommunityFileMap(map);
                        }
                    }
                })
                .storeFileAll(files);
    }
    
    // 해시태그 등록 공통 처리
    private void insertHashtags(CommunityDto communityDto) {
        if (communityDto.getHashtags() == null) {
            return;
        }

        for (String tagName : communityDto.getHashtags()) {

            tagName = normalizeTag(tagName);

            if (tagName.isBlank()) {
                continue;
            }

            Long tagNo = communityMapper.selectHashtagNo(tagName);

            if (tagNo == null) {
                communityMapper.insertHashtag(tagName);
                tagNo = communityMapper.selectHashtagNo(tagName);
            }

            communityMapper.insertCommunityHashtag(
                    communityDto.getCommNo(),
                    tagNo
            );
        }
    }
    
    // 커뮤니티 게시글 수정
    @Transactional
    public void modifyCommunity(CommunityDto communityDto, List<MultipartFile> files) {

        // 기존 이미지 중 유지할 이미지 경로
    	final List<String> keepImageUrls =
    	        communityDto.getExistingImageUrls() == null
    	                ? List.of()
    	                : communityDto.getExistingImageUrls();

        // 신규 파일 존재 여부
        boolean hasNewFiles = files != null && !files.isEmpty();
        if (files == null) {
            files = List.of();
        }

        // 핫플거리는 수정 시에도 이미지가 최소 1장 필요
        if ("CMM002".equals(communityDto.getCommCatCd())
                && keepImageUrls.isEmpty()
                && !hasNewFiles) {
            throw new IllegalArgumentException("핫플거리는 사진을 1장 이상 등록해야 합니다.");
        }

        // 기존 전체 이미지 경로 조회
        List<String> oldImageUrls = communityMapper.selectCommunityImages(communityDto.getCommNo());

        if (oldImageUrls == null) {
            oldImageUrls = List.of();
        }

        // 삭제 대상 = 기존 이미지 중 유지 목록에 없는 것
        final List<String> deleteImageUrls = oldImageUrls.stream()
                .filter(path -> !keepImageUrls.contains(path))
                .toList();
        // 유지할 기존 파일 번호 미리 조회
        final List<Long> keepFileNos =
                keepImageUrls.isEmpty()
                        ? List.of()
                        : communityMapper.selectCommunityFileNosByPaths(
                                communityDto.getCommNo(),
                                keepImageUrls
                        );

        // 대표 이미지 결정
        if (!keepImageUrls.isEmpty()) {
            communityDto.setCommMainImgUrl(keepImageUrls.get(0));
        }

        fileStorage.setup("community")
                .subPath(communityDto.getCommCatCd())
                .allow(List.of("jpg", "jpeg", "png", "gif", "webp"))
                .maxSize("10MB")
                .onSuccess(context -> {

                    List<FileUploadResult> savedFiles = context.getSavedFiles();

                    // 기존 유지 이미지가 없고 새 이미지가 있으면 첫 새 이미지가 대표 이미지
                    if (keepImageUrls.isEmpty()
                            && savedFiles != null
                            && !savedFiles.isEmpty()) {
                        communityDto.setCommMainImgUrl(savedFiles.get(0).getFilePath());
                    }

                    // 게시글 제목/내용/대표이미지 수정
                    communityMapper.updateCommunity(communityDto);

                    // 해시태그 재등록
                    communityMapper.deleteCommunityHashtags(communityDto.getCommNo());
                    insertHashtags(communityDto);

                    // 기존 파일 매핑/파일정보 정리
                    if (!deleteImageUrls.isEmpty()) {
                        List<Long> deleteFileNos =
                                communityMapper.selectCommunityFileNosByPaths(
                                        communityDto.getCommNo(),
                                        deleteImageUrls
                                );

                        if (deleteFileNos != null && !deleteFileNos.isEmpty()) {
                            communityMapper.deleteCommunityFileMapsByFileNos(
                                    communityDto.getCommNo(),
                                    deleteFileNos
                            );

                            communityMapper.deleteFiles(deleteFileNos);
                        }
                    }

                    // 기존 유지 이미지 매핑을 다시 정렬하기 위해 전체 매핑 삭제 후 재등록
                    communityMapper.deleteCommunityFileMaps(communityDto.getCommNo());

                    int sortOrder = 0;

                    // 유지 이미지 다시 매핑
                    if (!keepFileNos.isEmpty()) {

                        for (Long fileNo : keepFileNos) {

                            CommunityFileMapDto map = new CommunityFileMapDto();

                            map.setCommNo(communityDto.getCommNo());
                            map.setFileNo(fileNo);
                            map.setSortOrdr(sortOrder++);

                            communityMapper.insertCommunityFileMap(map);
                        }
                    }

                    // 새 파일 FILE 등록 + 매핑
                    if (savedFiles != null && !savedFiles.isEmpty()) {
                        for (FileUploadResult saved : savedFiles) {

                            FileDomain fileDomain = FileDomain.builder()
                                    .fileOrgNm(saved.getFileOrgNm())
                                    .fileSaveNm(saved.getFileSaveNm())
                                    .filePath(saved.getFilePath())
                                    .fileExt(saved.getFileExt())
                                    .fileSize(saved.getFileSize())
                                    .fileMimeType(saved.getContentType())
                                    .fileType("IMAGE")
                                    .build();

                            fileMapper.insertFile(fileDomain);

                            CommunityFileMapDto map = new CommunityFileMapDto();
                            map.setCommNo(communityDto.getCommNo());
                            map.setFileNo(fileDomain.getFileNo());
                            map.setSortOrdr(sortOrder++);

                            communityMapper.insertCommunityFileMap(map);
                        }
                    }
                })
                .updateFileAll(files, deleteImageUrls);
    }
    
    public void removeCommunity(Long commNo) {

        // 실제 파일 삭제용 이미지 경로 조회
        List<String> imagePaths =
                communityMapper.selectCommunityImages(commNo);

        // FILE 테이블 삭제용 파일 번호 목록 조회
        List<Long> fileNos = communityMapper.selectCommunityFileNos(commNo);

        // 좋아요 삭제
        communityMapper.deleteCommunityLikes(commNo);

        // 해시태그 연결 삭제
        communityMapper.deleteCommunityHashtags(commNo);

        // 파일 매핑 삭제
        communityMapper.deleteCommunityFileMaps(commNo);

        // FILE 테이블 삭제
        if (fileNos != null && !fileNos.isEmpty()) {
            communityMapper.deleteFiles(fileNos);
        }

        // 게시글 논리삭제
        communityMapper.deleteCommunity(commNo);

        // 실제 파일 삭제
        if (imagePaths != null && !imagePaths.isEmpty()) {
            fileStorage.setup("community")
                    .deleteFileAll(imagePaths);
        }
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
    
    // 커뮤니티 게시글 좋아요 여부 조회
    public boolean isLiked(Long commNo, Long mbrId) {
        return communityMapper.selectLikeCount(commNo, mbrId) > 0;
    }
    
    // 지역 조회
    public List<RegionDto> getRegionList() {
        return communityMapper.selectRegionList();
    }
    
    // 카테고리 조회
    public List<PlaceCategoryDto> getPlaceCategoryList() {
        return communityMapper.selectPlaceCategoryList();
    }
    
    // 구제척인 장소
    public List<PlaceDto> getPlaceList(Integer rgnCd, String catCd) {
        return communityMapper.selectPlaceList(rgnCd, catCd);
    }
    
    // 인기 해시태그 TOP5 조회
    public List<String> getPopularHashtags(String catCd) {
        return communityMapper.selectPopularHashtags(catCd);
    }
    
    // 게시글 목록 좋아요 여부 조회
    public List<Long> getLikedCommunityNos(List<Long> commNos, Long mbrId) {

        if (commNos == null || commNos.isEmpty()) {
            return List.of();
        }

        return communityMapper.selectLikedCommunityNos(commNos, mbrId);
    }
    
    // 해시태그 정리
    private String normalizeTag(String tagName) {
        if (tagName == null) {
            return "";
        }

        return tagName
                .trim()
                .replaceFirst("^#", "")
                .replaceAll("\\s+", "");
    }
}



/* 파일 업로더 클래스들*/
/** MemberService
 * FileStorage fileStorage는 건들게 없어요. (잘못건들면 기능 자체가 망가짐) - 파일관련 추가 / 삭제만 관장
 * 
 * FileMapper fileMapper :
 *  fileStorage 가 기능을 처리하고 결과값을 내줌. (파일 정보를 반환)
 *  후에 ->  FileMapper fileMapper에서 데이터 처리를 해줘야 함. (FILE TABLE) - 지금은 insert만 해놨음
 *  
 */

//사용법 - home/sst/attachment/member/favori/sdfsdf
// 반환값은 List<FileUploadResult>
// fileStorage.setup("comunity")
//.subPath("test");
//.allow(List.of("jpg", "jpeg", "png", "webp"))
// .maxSize("10MB") // 문자열도 됨 10MB, 1GB, 123454345 or 1024 * 1024 * 2 (바이트 계산해서 써도 됨) 쓰면 적용됨
// .onSuccess(context -> {} // 파일이 성공했을때 -> 파일이 제대로 들어간거임
// .onFailure(context -> {} // 파일이 실패했을때 -> 파일이 안들어간거임. 혹은 삭제중에 문제가 생긴거고
// context는 뭐냐 : sst.global.files.dto.FileActionContext 
//   -> savedFiles List<FileUploadResult> : 성공한것들
//   -> deletedFiles List<FileUploadResult> : 교체된 파일들
// FileActionContext 의 FileUploadResult는 뭐냐
/*
 .onSuccess(context -> {
	// 저장된 파일 정보
    List<FileUploadResult> saved = context.getSavedFiles();
    // 삭제된 파일 정보
    FileUploadResult removed = context.getDeletedFiles().get(0);
    
    // DB 구현부
    FileDomain fileDomain = FileDomain.builder()
            .fileOrgNm(saved.getFileOrgNm())
            .fileSaveNm(saved.getFileSaveNm())
            .filePath(saved.getFilePath())
            .fileExt(saved.getFileExt())
            .fileSize(saved.getFileSize())
            .fileMimeType(saved.getContentType())
            .fileType("IMAGE")
            .build();
    
    fileMapper.insertFile(fileDomain);
    
    if(fileDomain.getFileNo() != null) {
        memberMapper.updateMemberProfileFileNo(mbrId, fileDomain.getFileNo());
    }
})
 */


// .storeFile() - 파일 추가
// .replaceFile() - 파일 교체 단 교페파일이 없거나 개수가 안맞으면 처음으로 롤백
// .updateFile()  - 파일 교체 단 파일이 없으면 추가됨
// .deleteFile() - 파일 삭제
// -> 여기서 문제가 발생되면 delete빼고는 초기로 롤백(확인은해야됨)