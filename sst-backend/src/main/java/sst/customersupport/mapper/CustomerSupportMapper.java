package sst.customersupport.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import sst.customersupport.domain.CustomerSupport;

@Mapper
public interface CustomerSupportMapper {

    // 공지사항 조회
    List<CustomerSupport> selectNoticeList();

    // FAQ 조회
    List<CustomerSupport> selectFaqList();
}