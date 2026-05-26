package sst.customersupport.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sst.customersupport.domain.CustomerSupport;
import sst.customersupport.mapper.CustomerSupportMapper;

@Service
@RequiredArgsConstructor
public class CustomerSupportService {

    private final CustomerSupportMapper customerSupportMapper;

    // 공지사항 조회
    public List<CustomerSupport> getNoticeList() {
        return customerSupportMapper.selectNoticeList();
    }

    // FAQ 조회
    public List<CustomerSupport> getFaqList() {
        return customerSupportMapper.selectFaqList();
    }
}