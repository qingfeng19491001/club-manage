package com.clubmanage.service;

import com.clubmanage.common.TimeUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clubmanage.common.BusinessException;
import com.clubmanage.common.ErrorCode;
import com.clubmanage.dto.activity.ActivityCheckinRequest;
import com.clubmanage.dto.activity.CreateActivityRequest;
import com.clubmanage.entity.Activity;
import com.clubmanage.entity.Registration;
import com.clubmanage.mapper.ActivityMapper;
import com.clubmanage.mapper.RegistrationMapper;
import com.clubmanage.security.SecurityUtils;
import com.clubmanage.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityMapper activityMapper;
    private final RegistrationMapper registrationMapper;
    private final ClubMemberGuard clubMemberGuard;

    public Page<Activity> listActivities(int page, int size, Long clubId, Integer status) {
        LambdaQueryWrapper<Activity> q = new LambdaQueryWrapper<>();
        if (clubId != null) {
            q.eq(Activity::getClubId, clubId);
        }
        if (status != null) {
            q.eq(Activity::getStatus, status);
        } else {
            q.eq(Activity::getStatus, 1);
        }
        q.orderByDesc(Activity::getStartTime);
        return activityMapper.selectPage(new Page<>(page, size), q);
    }

    public Activity getActivity(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        return activity;
    }

    @Transactional
    public Activity createActivity(CreateActivityRequest request) {
        Long userId = SecurityUtils.currentUserId();
        clubMemberGuard.requireClubLeader(request.getClubId(), userId);
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "缂佹挻娼弮鍫曟？娑撳秷鍏橀弮鈺€绨鈧慨瀣闂?);
        }
        Activity activity = new Activity();
        activity.setClubId(request.getClubId());
        activity.setTitle(request.getTitle());
        activity.setDescription(request.getDescription());
        activity.setLocation(request.getLocation());
        activity.setLatitude(request.getLatitude());
        activity.setLongitude(request.getLongitude());
        activity.setStartTime(request.getStartTime());
        activity.setEndTime(request.getEndTime());
        activity.setMaxParticipants(request.getMaxParticipants() != null ? request.getMaxParticipants() : 0);
        activity.setRegisteredCount(0);
        activity.setStatus(1);
        activity.setCoverUrl(request.getCoverUrl());
        activity.setCreatedBy(userId);
        activity.setCreatedAt(TimeUtil.now());
        activity.setUpdatedAt(TimeUtil.now());
        activityMapper.insert(activity);
        return activity;
    }

    @Transactional
    public Registration register(Long activityId) {
        Activity activity = getActivity(activityId);
        if (activity.getStatus() == null || activity.getStatus() != 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "濞茶濮╂稉宥呭讲閹躲儱鎮?);
        }
        Long userId = SecurityUtils.currentUserId();
        clubMemberGuard.requireActiveMember(activity.getClubId(), userId);
        Registration existing = registrationMapper.selectOne(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getActivityId, activityId)
                .eq(Registration::getUserId, userId));
        if (existing != null) {
            if (existing.getStatus() != null && existing.getStatus() == 2) {
                existing.setStatus(1);
                existing.setUpdatedAt(TimeUtil.now());
                registrationMapper.updateById(existing);
                activity.setRegisteredCount(activity.getRegisteredCount() + 1);
                activity.setUpdatedAt(TimeUtil.now());
                activityMapper.updateById(activity);
                return existing;
            }
            throw new BusinessException(ErrorCode.BAD_REQUEST, "瀹稿弶濮ら崥宥堫嚉濞茶濮?);
        }
        int max = activity.getMaxParticipants() != null ? activity.getMaxParticipants() : 0;
        int count = activity.getRegisteredCount() != null ? activity.getRegisteredCount() : 0;
        if (max > 0 && count >= max) {
            throw new BusinessException(ErrorCode.ACTIVITY_FULL);
        }
        Registration reg = new Registration();
        reg.setActivityId(activityId);
        reg.setUserId(userId);
        reg.setStatus(1);
        reg.setCreatedAt(TimeUtil.now());
        reg.setUpdatedAt(TimeUtil.now());
        registrationMapper.insert(reg);
        activity.setRegisteredCount(count + 1);
        activity.setUpdatedAt(TimeUtil.now());
        activityMapper.updateById(activity);
        return reg;
    }

    @Transactional
    public Registration checkin(Long activityId, ActivityCheckinRequest request) {
        Activity activity = getActivity(activityId);
        Long userId = SecurityUtils.currentUserId();
        Registration reg = registrationMapper.selectOne(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getActivityId, activityId)
                .eq(Registration::getUserId, userId));
        if (reg == null || reg.getStatus() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "閺堫亝濮ら崥宥堫嚉濞茶濮?);
        }
        if (reg.getStatus() == 3) {
            throw new BusinessException(ErrorCode.ALREADY_CHECKED_IN);
        }
        if (reg.getStatus() != 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "閹躲儱鎮曞鎻掑絿濞?);
        }
        String now = TimeUtil.now();
        if (now.isBefore(activity.getStartTime()) || now.isAfter(activity.getEndTime())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "娑撳秴婀ú璇插З缁涙儳鍩岄弮鍫曟？閼煎啫娲块崘?);
        }
        if (activity.getLatitude() != null && activity.getLongitude() != null) {
            double dist = GeoUtils.distanceMeters(
                    GeoUtils.toDouble(activity.getLatitude()),
                    GeoUtils.toDouble(activity.getLongitude()),
                    GeoUtils.toDouble(request.getLatitude()),
                    GeoUtils.toDouble(request.getLongitude()));
            if (dist > 200) {
                throw new BusinessException(ErrorCode.OUT_OF_CHECKIN_RANGE);
            }
        }
        reg.setStatus(3);
        reg.setCheckedInAt(now);
        reg.setUpdatedAt(now);
        registrationMapper.updateById(reg);
        return reg;
    }
}