package sst.customersupport.service;

import java.util.List;
import sst.customersupport.domain.CustomerSupport;

public interface CustomerSupportService {

    // 공지사항 조회
    List<CustomerSupport> getNoticeList();

    // FAQ 조회
    List<CustomerSupport> getFaqList();
    
    // 공지사항 작성
    int createNotice(CustomerSupport customerSupport);

    // 공지사항 수정
    int modifyNotice(CustomerSupport customerSupport);

    // 공지사항 삭제
    int removeNotice(Long csNo);
    
    // 자주묻는 질문 작성
    int createFaq(CustomerSupport customerSupport);

    // 자주묻는 질문 수정
    int modifyFaq(CustomerSupport customerSupport);

    // 자주묻는 질문 삭제
    int removeFaq(Long csNo);
}