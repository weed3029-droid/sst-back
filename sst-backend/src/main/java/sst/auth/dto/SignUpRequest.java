package sst.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sst.member.domain.Member;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

	@NotBlank(message = "회원의 이메일은 필수 입력 값입니다.")
	@Email(message = "이메일형식에 어긋납니다.")
	private String memberEmail; 
	
	@NotBlank(message = "회원의 닉네임은 필수 입력 값입니다.")
	@Size(min = 2, max = 10, message = "회원의 닉네임은 최소 2글자이상 최대 10글자이하로 입력해야 합니다.")
	private String memberNickname;
	
	@NotBlank(message = "회원의 비밀번호는 필수 입력 값입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 최소 8글자이상 최대 20글자이하로 입력해야 합니다.")
	private String memberPassword; 
	
	@NotBlank(message = "회원의 이름은 필수 입력 값입니다.")
	@Size(min = 2, max = 20, message = "회원의 이름은 최소 2글자이상 입력해야합니다.")
	private String memberName;
	
	@NotBlank(message = "회원의 연락처는 필수 입력 값입니다.")
	@Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", 
    		 message = "핸드폰 번호 양식이 맞지 않습니다. (01x-xxx(x)-xxxx)")
	private String memberPhone; 
	
}























