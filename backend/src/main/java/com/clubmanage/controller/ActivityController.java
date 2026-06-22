package com.clubmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clubmanage.common.Result;
import com.clubmanage.dto.activity.ActivityCheckinRequest;
import com.clubmanage.dto.activity.CreateActivityRequest;
import com.clubmanage.entity.Activity;
import com.clubmanage.entity.Registration;
import com.clubmanage.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public Result<Page<Activity>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long clubId,
            @RequestParam(required = false) Integer status) {
        return Result.ok(activityService.listActivities(page, size, clubId, status));
    }

    @GetMapping("/{id}")
    public Result<Activity> detail(@PathVariable Long id) {
        return Result.ok(activityService.getActivity(id));
    }

    @PostMapping
    public Result<Activity> create(@Valid @RequestBody CreateActivityRequest request) {
        return Result.ok(activityService.createActivity(request));
    }

    @PostMapping("/{id}/register")
    public Result<Registration> register(@PathVariable Long id) {
        return Result.ok(activityService.register(id));
    }

    @PutMapping("/{id}/checkin")
    public Result<Registration> checkin(@PathVariable Long id, @Valid @RequestBody ActivityCheckinRequest request) {
        return Result.ok(activityService.checkin(id, request));
    }
}