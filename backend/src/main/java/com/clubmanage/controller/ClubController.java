package com.clubmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clubmanage.common.Result;
import com.clubmanage.dto.club.*;
import com.clubmanage.entity.Club;
import com.clubmanage.entity.Member;
import com.clubmanage.service.ClubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @GetMapping("/mine")
    public Result<List<Club>> myClubs() {
        return Result.ok(clubService.listMyClubs());
    }

    @GetMapping
    public Result<Page<Club>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return Result.ok(clubService.listClubs(page, size, status, keyword));
    }

    @GetMapping("/{id}")
    public Result<Club> detail(@PathVariable Long id) {
        return Result.ok(clubService.getClub(id));
    }

    @PostMapping
    public Result<Club> create(@Valid @RequestBody CreateClubRequest request) {
        return Result.ok(clubService.createClub(request));
    }

    @PutMapping("/{id}/approve")
    public Result<Club> approve(@PathVariable Long id, @Valid @RequestBody ApproveClubRequest request) {
        return Result.ok(clubService.approveClub(id, request));
    }

    @GetMapping("/{id}/members")
    public Result<List<Map<String, Object>>> members(
            @PathVariable Long id,
            @RequestParam(required = false) Integer status) {
        return Result.ok(clubService.listMembers(id, status));
    }

    @PostMapping("/{id}/join")
    public Result<Member> join(@PathVariable Long id, @Valid @RequestBody JoinClubRequest request) {
        return Result.ok(clubService.joinClub(id, request));
    }

    @PutMapping("/{clubId}/members/{memberId}/review")
    public Result<Member> reviewMember(
            @PathVariable Long clubId,
            @PathVariable Long memberId,
            @Valid @RequestBody ReviewMemberRequest request) {
        return Result.ok(clubService.reviewMember(clubId, memberId, request));
    }
}