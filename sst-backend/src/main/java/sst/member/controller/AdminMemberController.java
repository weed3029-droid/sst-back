package sst.member.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import sst.global.response.ApiResponse;
import sst.member.domain.Member;
import sst.member.service.AdminMemberService;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

	/*
	 * // 'ADMIN' 권한이 있어야만 접근 가능하게
	 * 
	 * @PreAuthorize("hasRole('ADMIN')")
	 * 
	 * @GetMapping public ResponseEntity<ApiResponse<List<Member>>> getAllMembers()
	 * { List<Member> members = adminMemberService.getAllMembers(); return
	 * ResponseEntity.ok(ApiResponse.success(members)); }
	 */
    
    
    /**
     * 관리자: 회원 강제 탈퇴
     * 시큐리티를 통해 어드민 권한(ROLE_ADMIN)을 가진 유저만 접근 가능하도록 차단
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> withdrawMember(@PathVariable("memberId") Long memberId) {
        adminMemberService.withdrawMemberByAdmin(memberId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * 🚀 공통 PageRequest를 파라미터로 직접 받습니다. (?page=1&size=10 이 자동 바인딩 됨)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Member>>> getMembers(PageRequest pageRequest) {
        // Map 대신 명확한 PageResponse<Member> 타입으로 반환합니다.
        PageResponse<Member> result = adminMemberService.getMembersPaged(pageRequest);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}