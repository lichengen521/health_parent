package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.health.dao.MemberDao;
import com.health.pojo.Member;
import com.health.service.MemberService;
import com.health.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;


    //根据手机号查询会员
    @Override
    public Member memberByTelephone(String telephone) {
        Member byTelephone = memberDao.findByTelephone(telephone);
        return byTelephone;
    }

    //添加会员
    @Override
    public void add(Member member) {
        String password = member.getPassword();
        if (password != null) {
            //将明文使用MD5加密
            password = MD5Utils.md5(password);
            member.setPassword(password);
        }
        memberDao.add(member);
    }

    //根据月份查询会员数量
    @Override
    public List<Integer> findMemberCountByMonths(List<String> months) {

        List<Integer> memberList = new ArrayList<>();

        for (String month : months) {
            String date = month + ".31";
            Integer memberCountBeforeDate = memberDao.findMemberCountBeforeDate(date);
            memberList.add(memberCountBeforeDate);
        }

        return memberList;
    }
}
