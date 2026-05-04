package sst.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sst.global.response.ApiResponse;
import sst.global.security.domain.CustomUserDetails;
import sst.member.domain.Member;
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
}
