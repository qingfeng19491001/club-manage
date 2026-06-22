package com.clubmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clubmanage.common.Result;
import com.clubmanage.dto.announcement.CreateAnnouncementRequest;
import com.clubmanage.entity.Announcement;
import com.clubmanage.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public Result<Page<Announcement>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long clubId) {
        return Result.ok(announcementService.listAnnouncements(page, size, clubId));
    }

    @GetMapping("/{id}")
    public Result<Announcement> detail(@PathVariable Long id) {
        return Result.ok(announcementService.getAnnouncement(id));
    }

    @PostMapping
    public Result<Announcement> create(@Valid @RequestBody CreateAnnouncementRequest request) {
        return Result.ok(announcementService.create(request));
    }
}