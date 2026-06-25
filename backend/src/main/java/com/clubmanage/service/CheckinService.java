package com.clubmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clubmanage.common.BusinessException;
import com.clubmanage.common.ErrorCode;
import com.clubmanage.common.TimeUtil;
import com.clubmanage.dto.checkin.CheckinAppealRequest;
import com.clubmanage.dto.checkin.CheckinRecordRequest;
import com.clubmanage.dto.checkin.CreateCheckinTaskRequest;
import com.clubmanage.entity.CheckinRecord;
import com.clubmanage.entity.CheckinTask;
import com.clubmanage.mapper.CheckinRecordMapper;
import com.clubmanage.mapper.CheckinTaskMapper;
import com.clubmanage.security.SecurityUtils;
import com.clubmanage.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckinService {

    private final CheckinTaskMapper checkinTaskMapper;
    private final CheckinRecordMapper checkinRecordMapper;
    private final ClubMemberGuard clubMemberGuard;

    @Value("${club.checkin.default-radius-meters:200}")
    private int defaultRadiusMeters;

    public Page<CheckinTask> listTasks(int page, int size, Long clubId) {
        LambdaQueryWrapper<CheckinTask> q = new LambdaQueryWrapper<>();
        if (clubId != null) {
            q.eq(CheckinTask::getClubId, clubId);
        }
        q.orderByDesc(CheckinTask::getStartTime);
        return checkinTaskMapper.selectPage(new Page<>(page, size), q);
    }

    @Transactional
    public CheckinTask createTask(CreateCheckinTaskRequest request) {
        Long userId = SecurityUtils.currentUserId();
        clubMemberGuard.requireClubLeader(request.getClubId(), userId);
        if (TimeUtil.parse(request.getEndTime()).isBefore(TimeUtil.parse(request.getStartTime()))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "结束时间不能早于开始时间");
        }
        CheckinTask task = new CheckinTask();
        task.setClubId(request.getClubId());
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setLocationName(request.getLocationName());
        task.setLatitude(request.getLatitude());
        task.setLongitude(request.getLongitude());
        task.setRadiusMeters(request.getRadiusMeters() != null ? request.getRadiusMeters() : defaultRadiusMeters);
        task.setStartTime(request.getStartTime());
        task.setEndTime(request.getEndTime());
        task.setCreatedBy(userId);
        task.setCreatedAt(TimeUtil.now());
        task.setUpdatedAt(TimeUtil.now());
        checkinTaskMapper.insert(task);
        return task;
    }

    @Transactional
    public CheckinRecord submitRecord(CheckinRecordRequest request) {
        CheckinTask task = checkinTaskMapper.selectById(request.getTaskId());
        if (task == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        Long userId = SecurityUtils.currentUserId();
        clubMemberGuard.requireActiveMember(task.getClubId(), userId);
        CheckinRecord existing = checkinRecordMapper.selectOne(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getTaskId, request.getTaskId())
                .eq(CheckinRecord::getUserId, userId));
        if (existing != null && existing.getStatus() != null
                && (existing.getStatus() == 1 || existing.getStatus() == 3)) {
            throw new BusinessException(ErrorCode.ALREADY_CHECKED_IN);
        }
        String now = TimeUtil.now();
        if (TimeUtil.parse(now).isBefore(TimeUtil.parse(task.getStartTime())) || TimeUtil.parse(now).isAfter(TimeUtil.parse(task.getEndTime()))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不在签到时间范围内");
        }
        int radius = task.getRadiusMeters() != null ? task.getRadiusMeters() : defaultRadiusMeters;
        double dist = GeoUtils.distanceMeters(
                GeoUtils.toDouble(task.getLatitude()),
                GeoUtils.toDouble(task.getLongitude()),
                GeoUtils.toDouble(request.getLatitude()),
                GeoUtils.toDouble(request.getLongitude()));
        if (dist > radius) {
            throw new BusinessException(ErrorCode.OUT_OF_CHECKIN_RANGE);
        }
        CheckinRecord record = existing != null ? existing : new CheckinRecord();
        record.setTaskId(request.getTaskId());
        record.setUserId(userId);
        record.setLatitude(request.getLatitude());
        record.setLongitude(request.getLongitude());
        record.setStatus(1);
        record.setCheckedAt(now);
        record.setUpdatedAt(now);
        if (existing == null) {
            record.setCreatedAt(now);
            checkinRecordMapper.insert(record);
        } else {
            checkinRecordMapper.updateById(record);
        }
        return record;
    }

    @Transactional
    public CheckinRecord appeal(CheckinAppealRequest request) {
        CheckinTask task = checkinTaskMapper.selectById(request.getTaskId());
        if (task == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        Long userId = SecurityUtils.currentUserId();
        clubMemberGuard.requireActiveMember(task.getClubId(), userId);
        String now = TimeUtil.now();
        if (TimeUtil.parse(now).isBefore(TimeUtil.parse(task.getStartTime())) || TimeUtil.parse(now).isAfter(TimeUtil.parse(task.getEndTime()))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不在签到时间范围内");
        }
        CheckinRecord record = checkinRecordMapper.selectOne(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getTaskId, request.getTaskId())
                .eq(CheckinRecord::getUserId, userId));
        if (record != null && record.getStatus() != null
                && (record.getStatus() == 1 || record.getStatus() == 3)) {
            throw new BusinessException(ErrorCode.ALREADY_CHECKED_IN);
        }
        if (record == null) {
            record = new CheckinRecord();
            record.setTaskId(request.getTaskId());
            record.setUserId(userId);
            record.setCreatedAt(now);
        }
        record.setLatitude(request.getLatitude());
        record.setLongitude(request.getLongitude());
        record.setStatus(2);
        record.setAppealReason(request.getAppealReason());
        record.setCheckedAt(now);
        record.setUpdatedAt(now);
        if (record.getId() == null) {
            checkinRecordMapper.insert(record);
        } else {
            checkinRecordMapper.updateById(record);
        }
        return record;
    }

    public Map<String, Object> stats(Long clubId, Long taskId) {
        Long userId = SecurityUtils.currentUserId();
        if (taskId != null) {
            CheckinTask task = checkinTaskMapper.selectById(taskId);
            if (task == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND);
            }
            clubMemberGuard.requireClubLeader(task.getClubId(), userId);
        } else if (clubId != null) {
            clubMemberGuard.requireClubLeader(clubId, userId);
        } else {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "请指定 clubId 或 taskId");
        }
        LambdaQueryWrapper<CheckinRecord> q = new LambdaQueryWrapper<>();
        if (taskId != null) {
            q.eq(CheckinRecord::getTaskId, taskId);
        } else if (clubId != null) {
            List<CheckinTask> tasks = checkinTaskMapper.selectList(
                    new LambdaQueryWrapper<CheckinTask>().eq(CheckinTask::getClubId, clubId));
            if (tasks.isEmpty()) {
                return Map.of("total", 0, "success", 0, "appealPending", 0);
            }
            List<Long> taskIds = tasks.stream().map(CheckinTask::getId).toList();
            q.in(CheckinRecord::getTaskId, taskIds);
        }
        List<CheckinRecord> records = checkinRecordMapper.selectList(q);
        long success = records.stream().filter(r -> r.getStatus() != null && (r.getStatus() == 1 || r.getStatus() == 3)).count();
        long appealPending = records.stream().filter(r -> r.getStatus() != null && r.getStatus() == 2).count();
        Map<String, Object> map = new HashMap<>();
        map.put("total", records.size());
        map.put("success", success);
        map.put("appealPending", appealPending);
        return map;
    }
}
