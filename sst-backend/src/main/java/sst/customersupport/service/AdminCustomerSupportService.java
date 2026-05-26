package sst.customersupport.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sst.customersupport.domain.CustomerSupport;
import sst.customersupport.mapper.CustomerSupportMapper;

@Service
@RequiredArgsConstructor
public class AdminCustomerSupportService {

    private final CustomerSupportMapper customerSupportMapper;

    // 공지사항 작성
    public int createNotice(CustomerSupport customerSupport) {
        return customerSupportMapper.insertNotice(customerSupport);
    }

    // 공지사항 수정
    public int updateNotice(CustomerSupport customerSupport) {
        return customerSupportMapper.updateNotice(customerSupport);
    }

    // 공지사항 삭제
    public int deleteNotice(Long csNo) {
        return customerSupportMapper.deleteNotice(csNo);
    }

    // FAQ 작성
    public int createFaq(CustomerSupport customerSupport) {
        return customerSupportMapper.insertFaq(customerSupport);
    }

    // FAQ 수정
    public int updateFaq(CustomerSupport customerSupport) {
        return customerSupportMapper.updateFaq(customerSupport);
    }

    // FAQ 삭제
    public int deleteFaq(Long csNo) {
        return customerSupportMapper.deleteFaq(csNo);
    }
}