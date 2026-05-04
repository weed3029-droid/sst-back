package sst.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sst.global.response.ApiResponse;
import sst.global.security.domain.CustomUserDetails;
import sst.member.domain.Member;
import sst.member.dto.MemberUpdateRequest;
import sst.member.service.MemberService;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
	
	private final MemberService memberService;

	//@PreAuthorize("hasRole('ADMIN')")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/{email}")
	public ResponseEntity<ApiResponse<Member>> getMemberInfoByEmail(@PathVariable(value="email") String email){
		
		Member member = memberService.getMemberInfoByEmail(email);
		
		return ResponseEntity.ok(ApiResponse.success(member));
	}
	
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<Member>> getMemberInfoByEmail(@AuthenticationPrincipal CustomUserDetails userDetails){
		
		return ResponseEntity.ok(ApiResponse.success(userDetails.getMember()));
	}
	
	/**
     * 내 정보 수정
     */
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberUpdateRequest request) {

        memberService.updateMemberInfo(userDetails.getMember().getMbrId(), request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
