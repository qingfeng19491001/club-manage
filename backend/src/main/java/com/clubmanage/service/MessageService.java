package com.clubmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clubmanage.common.BusinessException;
import com.clubmanage.common.ErrorCode;
import com.clubmanage.entity.Message;
import com.clubmanage.mapper.MessageMapper;
import com.clubmanage.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;

    public Page<Message> listMessages(int page, int size, Integer isRead) {
        Long userId = SecurityUtils.currentUserId();
        LambdaQueryWrapper<Message> q = new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId);
        if (isRead != null) {
            q.eq(Message::getIsRead, isRead);
        }
        q.orderByDesc(Message::getCreatedAt);
        return messageMapper.selectPage(new Page<>(page, size), q);
    }

    @Transactional
    public Message markRead(Long messageId) {
        Long userId = SecurityUtils.currentUserId();
        Message msg = messageMapper.selectById(messageId);
        if (msg == null || !msg.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        if (msg.getIsRead() == null || msg.getIsRead() == 0) {
            msg.setIsRead(1);
            messageMapper.updateById(msg);
        }
        return msg;
    }
}