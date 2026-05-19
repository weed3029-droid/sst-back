package sst.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sst.content.dto.WishDto;
import sst.content.dto.WishResponseDto;
import sst.content.mapper.WishMapper;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishService {

    private final WishMapper wishMapper;

    @Transactional
    public boolean toggleWish(WishDto dto) {
        int count = wishMapper.checkWish(dto);
        if (count > 0) {
            wishMapper.deleteWish(dto);
            return false;
        } else {
            wishMapper.insertWish(dto);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public boolean checkWish(WishDto dto) {
        return wishMapper.checkWish(dto) > 0;
    }

    @Transactional(readOnly = true)
    public List<WishResponseDto> getMyWishlist(Long mbrId) {
        return wishMapper.selectMyWishlist(mbrId);
    }

    // 여러 장소 찜 상태 한번에 조회
    @Transactional(readOnly = true)
    public List<Long> getWishedPlcNos(Long mbrId, List<Long> plcNos) {
        if (mbrId == null || plcNos == null || plcNos.isEmpty()) return List.of();
        return wishMapper.selectWishedPlcNos(mbrId, plcNos);
    }
}