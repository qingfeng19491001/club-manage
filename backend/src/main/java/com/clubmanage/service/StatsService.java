package com.clubmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clubmanage.entity.Activity;
import com.clubmanage.entity.Club;
import com.clubmanage.entity.Member;
import com.clubmanage.mapper.ActivityMapper;
import com.clubmanage.mapper.ClubMapper;
import com.clubmanage.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final ClubMapper clubMapper;
    private final MemberMapper memberMapper;
    private final ActivityMapper activityMapper;

    public Map<String, Object> overview() {
        Long totalClubs = clubMapper.selectCount(new LambdaQueryWrapper<Club>()
                .eq(Club::getStatus, 1));
        Long totalMembers = memberMapper.selectCount(new LambdaQueryWrapper<Member>()
                .eq(Member::getStatus, 1));
        Long totalActivities = activityMapper.selectCount(new LambdaQueryWrapper<Activity>());

        List<Club> topClubs = clubMapper.selectList(new LambdaQueryWrapper<Club>()
                .eq(Club::getStatus, 1)
                .orderByDesc(Club::getMemberCount)
                .last("LIMIT 5"));

        Map<String, Integer> clubByCategory = new LinkedHashMap<>();
        clubByCategory.put("全部", totalClubs == null ? 0 : totalClubs.intValue());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalClubs", totalClubs == null ? 0 : totalClubs);
        data.put("totalMembers", totalMembers == null ? 0 : totalMembers);
        data.put("totalActivities", totalActivities == null ? 0 : totalActivities);
        data.put("clubByCategory", clubByCategory);
        data.put("topClubs", topClubs);
        return data;
    }
}