package com.health.service;

import com.health.pojo.Member;

import java.util.List;
import java.util.Map;

public interface MemberService {

    //根据手机号查询会员
    Member memberByTelephone(String telephone);

    //添加会员
    void add(Member member);

    //根据月份查询会员数量
    List<Integer> findMemberCountByMonths(List<String> months);

}
