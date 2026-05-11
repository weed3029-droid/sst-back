package sst.global.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 🚀 공통 페이징 요청 DTO
 * 프론트엔드에서 넘어오는 page, size 파라미터를 받고
 * MyBatis에서 사용할 offset을 자동 계산해주는 객체입니다.
 */
@Getter
@Setter
public class PageRequest {
    private int page = 1;      // 기본 1페이지
    private int size = 10;     // 기본 10개씩

    // 🚀 MyBatis Mapper에서 #{offset} 으로 바로 꺼내 쓸 수 있도록 자동 계산!
    public int getOffset() {
        return (this.page - 1) * this.size;
    }
}