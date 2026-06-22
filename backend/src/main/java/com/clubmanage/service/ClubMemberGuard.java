package com.clubmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clubmanage.common.BusinessException;
import com.clubmanage.common.ErrorCode;
import com.clubmanage.entity.Member;
import com.clubmanage.mapper.MemberMapper;
import com.clubmanage.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClubMemberGuard {

    private final MemberMapper memberMapper;

    public Member requireActiveMember(Long clubId, Long userId) {
        Member member = memberMapper.selectOne(new LambdaQueryWrapper<Member>()
                .eq(Member::getClubId, clubId)
                .eq(Member::getUserId, userId));
        if (member == null || member.getStatus() == null || member.getStatus() != 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "非社团成员");
        }
        return member;
    }

    public void requireClubLeader(Long clubId, Long userId) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        Member member = requireActiveMember(clubId, userId);
        if (member.getRole() == null || member.getRole() < 2) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "需要社团负责人权限");
        }
    }

    public void requireAdmin() {
        if (!SecurityUtils.isAdmin()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}