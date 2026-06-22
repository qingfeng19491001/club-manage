package com.clubmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clubmanage.common.BusinessException;
import com.clubmanage.common.ErrorCode;
import com.clubmanage.dto.club.*;
import com.clubmanage.entity.Club;
import com.clubmanage.entity.Member;
import com.clubmanage.entity.Message;
import com.clubmanage.entity.User;
import com.clubmanage.mapper.ClubMapper;
import com.clubmanage.mapper.MemberMapper;
import com.clubmanage.mapper.MessageMapper;
import com.clubmanage.mapper.UserMapper;
import com.clubmanage.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubMapper clubMapper;
    private final MemberMapper memberMapper;
    private final UserMapper userMapper;
    private final MessageMapper messageMapper;
    private final ClubMemberGuard clubMemberGuard;

    public List<Club> listMyClubs() {
        Long userId = SecurityUtils.currentUserId();
        List<Member> memberships = memberMapper.selectList(new LambdaQueryWrapper<Member>()
                .eq(Member::getUserId, userId)
                .eq(Member::getStatus, 1));
        if (memberships.isEmpty()) {
            return List.of();
        }
        List<Long> clubIds = memberships.stream().map(Member::getClubId).distinct().toList();
        return clubMapper.selectBatchIds(clubIds);
    }

    public Page<Club> listClubs(int page, int size, Integer status, String keyword) {
        LambdaQueryWrapper<Club> q = new LambdaQueryWrapper<>();
        if (status != null) {
            q.eq(Club::getStatus, status);
        } else {
            q.eq(Club::getStatus, 1);
        }
        if (keyword != null && !keyword.isBlank()) {
            q.like(Club::getName, keyword.trim());
        }
        q.orderByDesc(Club::getCreatedAt);
        return clubMapper.selectPage(new Page<>(page, size), q);
    }

    public Club getClub(Long id) {
        Club club = clubMapper.selectById(id);
        if (club == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        return club;
    }

    @Transactional
    public Club createClub(CreateClubRequest request) {
        Long count = clubMapper.selectCount(new LambdaQueryWrapper<Club>()
                .eq(Club::getName, request.getName()));
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.CLUB_NAME_EXISTS);
        }
        Long userId = SecurityUtils.currentUserId();
        Club club = new Club();
        club.setName(request.getName());
        club.setDescription(request.getDescription());
        club.setLogoUrl(request.getLogoUrl());
        club.setFounderId(userId);
        club.setStatus(0);
        club.setMemberCount(0);
        club.setCreatedAt(LocalDateTime.now());
        club.setUpdatedAt(LocalDateTime.now());
        clubMapper.insert(club);
        return club;
    }

    @Transactional
    public Club approveClub(Long clubId, ApproveClubRequest request) {
        clubMemberGuard.requireAdmin();
        Club club = getClub(clubId);
        if (club.getStatus() != 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "社团已审核");
        }
        if (Boolean.TRUE.equals(request.getApproved())) {
            club.setStatus(1);
            club.setRejectReason(null);
            club.setUpdatedAt(LocalDateTime.now());
            clubMapper.updateById(club);

            Member leader = new Member();
            leader.setClubId(clubId);
            leader.setUserId(club.getFounderId());
            leader.setRole(2);
            leader.setStatus(1);
            leader.setJoinedAt(LocalDateTime.now());
            leader.setCreatedAt(LocalDateTime.now());
            leader.setUpdatedAt(LocalDateTime.now());
            memberMapper.insert(leader);

            club.setMemberCount(1);
            clubMapper.updateById(club);

            User founder = userMapper.selectById(club.getFounderId());
            if (founder != null && (founder.getRole() == null || founder.getRole() < 1)) {
                founder.setRole(1);
                founder.setUpdatedAt(LocalDateTime.now());
                userMapper.updateById(founder);
            }
            notifyUser(club.getFounderId(), "社团审核通过", "您的社团「" + club.getName() + "」已通过审核。", 1, clubId);
        } else {
            club.setStatus(2);
            club.setRejectReason(request.getRejectReason());
            club.setUpdatedAt(LocalDateTime.now());
            clubMapper.updateById(club);
            notifyUser(club.getFounderId(), "社团审核未通过", "您的社团「" + club.getName() + "」未通过审核。"
                    + (request.getRejectReason() != null ? " 原因：" + request.getRejectReason() : ""), 1, clubId);
        }
        return clubMapper.selectById(clubId);
    }

    @Transactional
    public Member joinClub(Long clubId, JoinClubRequest request) {
        Club club = getClub(clubId);
        if (club.getStatus() != 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "社团未成立");
        }
        Long userId = SecurityUtils.currentUserId();
        Member existing = memberMapper.selectOne(new LambdaQueryWrapper<Member>()
                .eq(Member::getClubId, clubId)
                .eq(Member::getUserId, userId));
        if (existing != null) {
            if (existing.getStatus() == 1) {
                throw new BusinessException(ErrorCode.ALREADY_JOINED_CLUB);
            }
            if (existing.getStatus() == 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "入社申请审核中");
            }
            existing.setStatus(0);
            existing.setApplyReason(request.getApplyReason());
            existing.setRejectReason(null);
            existing.setUpdatedAt(LocalDateTime.now());
            memberMapper.updateById(existing);
            return existing;
        }
        Member member = new Member();
        member.setClubId(clubId);
        member.setUserId(userId);
        member.setRole(0);
        member.setStatus(0);
        member.setApplyReason(request.getApplyReason());
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        memberMapper.insert(member);
        return member;
    }

    public List<Map<String, Object>> listMembers(Long clubId, Integer status) {
        getClub(clubId);
        LambdaQueryWrapper<Member> q = new LambdaQueryWrapper<Member>().eq(Member::getClubId, clubId);
        if (status != null) {
            q.eq(Member::getStatus, status);
        }
        List<Member> members = memberMapper.selectList(q.orderByDesc(Member::getCreatedAt));
        if (members.isEmpty()) {
            return List.of();
        }
        Set<Long> userIds = members.stream().map(Member::getUserId).collect(Collectors.toSet());
        Map<Long, User> users = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Member m : members) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("member", m);
            User u = users.get(m.getUserId());
            if (u != null) {
                row.put("username", u.getUsername());
                row.put("realName", u.getRealName());
                row.put("avatarUrl", u.getAvatarUrl());
            }
            result.add(row);
        }
        return result;
    }

    @Transactional
    public Member reviewMember(Long clubId, Long memberId, ReviewMemberRequest request) {
        clubMemberGuard.requireClubLeader(clubId, SecurityUtils.currentUserId());
        Member member = memberMapper.selectById(memberId);
        if (member == null || !member.getClubId().equals(clubId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        if (member.getStatus() != 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "申请已处理");
        }
        Club club = getClub(clubId);
        if (Boolean.TRUE.equals(request.getApproved())) {
            member.setStatus(1);
            member.setJoinedAt(LocalDateTime.now());
            member.setRejectReason(null);
            club.setMemberCount(club.getMemberCount() + 1);
            clubMapper.updateById(club);
            notifyUser(member.getUserId(), "入社申请通过", "您已成功加入社团「" + club.getName() + "」。", 1, clubId);
        } else {
            member.setStatus(2);
            member.setRejectReason(request.getRejectReason());
            notifyUser(member.getUserId(), "入社申请未通过", "您的入社申请未通过。"
                    + (request.getRejectReason() != null ? " 原因：" + request.getRejectReason() : ""), 1, clubId);
        }
        member.setUpdatedAt(LocalDateTime.now());
        memberMapper.updateById(member);
        return member;
    }

    private void notifyUser(Long userId, String title, String content, int type, Long refId) {
        Message msg = new Message();
        msg.setUserId(userId);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setType(type);
        msg.setRefId(refId);
        msg.setIsRead(0);
        msg.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(msg);
    }
}