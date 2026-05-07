package sst.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sst.global.response.ApiResponse;
import sst.member.domain.Member;
import sst.member.service.AdminMemberService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    // 'ADMIN' 권한이 있어야만 접근 가능하게 
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Member>>> getAllMembers() {
        List<Member> members = adminMemberService.getAllMembers();
        return ResponseEntity.ok(ApiResponse.success(members));
    }
    
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/{mbrId}")
//    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable("mbrId") Long mbrId) {
//        adminMemberService.forceDeleteMember(mbrId);
//        return ResponseEntity.ok(ApiResponse.success(null));
//    }
}