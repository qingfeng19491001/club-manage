package com.clubmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clubmanage.common.BusinessException;
import com.clubmanage.common.ErrorCode;
import com.clubmanage.dto.announcement.CreateAnnouncementRequest;
import com.clubmanage.entity.Announcement;
import com.clubmanage.mapper.AnnouncementMapper;
import com.clubmanage.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementMapper announcementMapper;
    private final ClubMemberGuard clubMemberGuard;

    public Page<Announcement> listAnnouncements(int page, int size, Long clubId) {
        LambdaQueryWrapper<Announcement> q = new LambdaQueryWrapper<>();
        if (clubId != null) {
            q.eq(Announcement::getClubId, clubId);
        }
        q.orderByDesc(Announcement::getIsPinned).orderByDesc(Announcement::getCreatedAt);
        return announcementMapper.selectPage(new Page<>(page, size), q);
    }

    public Announcement getAnnouncement(Long id) {
        Announcement a = announcementMapper.selectById(id);
        if (a == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        return a;
    }

    @Transactional
    public Announcement create(CreateAnnouncementRequest request) {
        Long userId = SecurityUtils.currentUserId();
        clubMemberGuard.requireClubLeader(request.getClubId(), userId);
        Announcement a = new Announcement();
        a.setClubId(request.getClubId());
        a.setTitle(request.getTitle());
        a.setContent(request.getContent());
        a.setIsPinned(Boolean.TRUE.equals(request.getPinned()) ? 1 : 0);
        a.setCreatedBy(userId);
        a.setCreatedAt(LocalDateTime.now());
        a.setUpdatedAt(LocalDateTime.now());
        announcementMapper.insert(a);
        return a;
    }
}