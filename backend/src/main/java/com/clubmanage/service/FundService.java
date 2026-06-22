package com.clubmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clubmanage.common.BusinessException;
import com.clubmanage.common.ErrorCode;
import com.clubmanage.dto.fund.ApproveFundRequest;
import com.clubmanage.dto.fund.CreateFundRequest;
import com.clubmanage.entity.Fund;
import com.clubmanage.mapper.FundMapper;
import com.clubmanage.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FundService {

    private final FundMapper fundMapper;
    private final ClubMemberGuard clubMemberGuard;

    public Page<Fund> listFunds(int page, int size, Long clubId, Integer status) {
        LambdaQueryWrapper<Fund> q = new LambdaQueryWrapper<>();
        if (clubId != null) {
            q.eq(Fund::getClubId, clubId);
        }
        if (status != null) {
            q.eq(Fund::getStatus, status);
        }
        q.orderByDesc(Fund::getCreatedAt);
        return fundMapper.selectPage(new Page<>(page, size), q);
    }

    @Transactional
    public Fund createFund(CreateFundRequest request) {
        if (request.getType() == null || (request.getType() != 1 && request.getType() != 2)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "经费类型无效");
        }
        Long userId = SecurityUtils.currentUserId();
        clubMemberGuard.requireClubLeader(request.getClubId(), userId);
        Fund fund = new Fund();
        fund.setClubId(request.getClubId());
        fund.setTitle(request.getTitle());
        fund.setAmount(request.getAmount());
        fund.setType(request.getType());
        fund.setDescription(request.getDescription());
        fund.setStatus(0);
        fund.setApplicantId(userId);
        fund.setCreatedAt(LocalDateTime.now());
        fund.setUpdatedAt(LocalDateTime.now());
        fundMapper.insert(fund);
        return fund;
    }

    @Transactional
    public Fund approveFund(Long fundId, ApproveFundRequest request) {
        Fund fund = fundMapper.selectById(fundId);
        if (fund == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        clubMemberGuard.requireClubLeader(fund.getClubId(), SecurityUtils.currentUserId());
        if (fund.getStatus() == null || fund.getStatus() != 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "经费已审批");
        }
        Long approverId = SecurityUtils.currentUserId();
        if (Boolean.TRUE.equals(request.getApproved())) {
            fund.setStatus(1);
            fund.setRejectReason(null);
        } else {
            fund.setStatus(2);
            fund.setRejectReason(request.getRejectReason());
        }
        fund.setApproverId(approverId);
        fund.setUpdatedAt(LocalDateTime.now());
        fundMapper.updateById(fund);
        return fund;
    }
}