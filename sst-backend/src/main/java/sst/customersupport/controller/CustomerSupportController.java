package sst.customersupport.controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import sst.customersupport.domain.CustomerSupport;
import sst.customersupport.service.CustomerSupportService;

@RestController
@RequiredArgsConstructor
public class CustomerSupportController {

    private final CustomerSupportService customerSupportService;

    // 공지사항 조회
    @GetMapping("/api/customersupport/notice")
    public List<CustomerSupport> noticeList() {

        return customerSupportService.getNoticeList();
    }

    // FAQ 조회
    @GetMapping("/api/customersupport/faq")
    public List<CustomerSupport> faqList() {

        return customerSupportService.getFaqList();
    }
    
    // 공지사항 작성
    @PostMapping("/api/admin/customersupport/notice")
    public int createNotice(@RequestBody CustomerSupport customerSupport) {

        return customerSupportService.createNotice(customerSupport);
    }

    /// 공지사항 수정
    @PutMapping("/api/admin/customersupport/notice/{csNo}")
    public int modifyNotice(
            @PathVariable("csNo") Long csNo,
            @RequestBody CustomerSupport customerSupport) {

        customerSupport.setCsNo(csNo);

        return customerSupportService.modifyNotice(customerSupport);
    }

    // 공지사항 삭제
    @DeleteMapping("/api/admin/customersupport/notice/{csNo}")
    public int removeNotice(@PathVariable("csNo") Long csNo) {

        return customerSupportService.removeNotice(csNo);
    }
    
    // FAQ 작성
    @PostMapping("/api/admin/customersupport/faq")
    public int createFaq(@RequestBody CustomerSupport customerSupport) {
        return customerSupportService.createFaq(customerSupport);
    }

    // FAQ 수정
    @PutMapping("/api/admin/customersupport/faq/{csNo}")
    public int modifyFaq(
            @PathVariable("csNo") Long csNo,
            @RequestBody CustomerSupport customerSupport) {

        customerSupport.setCsNo(csNo);

        return customerSupportService.modifyFaq(customerSupport);
    }

    // FAQ 삭제
    @DeleteMapping("/api/admin/customersupport/faq/{csNo}")
    public int removeFaq(@PathVariable("csNo") Long csNo) {
        return customerSupportService.removeFaq(csNo);
    }
 
    
}