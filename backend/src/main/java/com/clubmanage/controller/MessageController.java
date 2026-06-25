package com.clubmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clubmanage.common.Result;
import com.clubmanage.entity.Message;
import com.clubmanage.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public Result<Page<Message>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer isRead) {
        return Result.ok(messageService.listMessages(page, size, isRead));
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Long>> unreadCount() {
        return Result.ok(Map.of("count", messageService.unreadCount()));
    }

    @PutMapping("/read-all")
    public Result<Void> markAllRead() {
        messageService.markAllRead();
        return Result.ok(null);
    }

    @PutMapping("/{id}/read")
    public Result<Message> markRead(@PathVariable Long id) {
        return Result.ok(messageService.markRead(id));
    }
}
