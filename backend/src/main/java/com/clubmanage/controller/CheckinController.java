package com.clubmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clubmanage.common.Result;
import com.clubmanage.dto.checkin.CheckinAppealRequest;
import com.clubmanage.dto.checkin.CheckinRecordRequest;
import com.clubmanage.dto.checkin.CreateCheckinTaskRequest;
import com.clubmanage.entity.CheckinRecord;
import com.clubmanage.entity.CheckinTask;
import com.clubmanage.service.CheckinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/checkin")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;

    @GetMapping("/tasks")
    public Result<Page<CheckinTask>> listTasks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long clubId) {
        return Result.ok(checkinService.listTasks(page, size, clubId));
    }

    @PostMapping("/tasks")
    public Result<CheckinTask> createTask(@Valid @RequestBody CreateCheckinTaskRequest request) {
        return Result.ok(checkinService.createTask(request));
    }

    @PostMapping("/records")
    public Result<CheckinRecord> submitRecord(@Valid @RequestBody CheckinRecordRequest request) {
        return Result.ok(checkinService.submitRecord(request));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(
            @RequestParam(required = false) Long clubId,
            @RequestParam(required = false) Long taskId) {
        return Result.ok(checkinService.stats(clubId, taskId));
    }

    @PostMapping("/appeal")
    public Result<CheckinRecord> appeal(@Valid @RequestBody CheckinAppealRequest request) {
        return Result.ok(checkinService.appeal(request));
    }
}