package com.clubmanage.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(200, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    USERNAME_EXISTS(1001, "用户名已存在"),
    PASSWORD_WRONG(1002, "密码错误"),
    CLUB_NAME_EXISTS(1003, "社团名称已存在"),
    ALREADY_JOINED_CLUB(1004, "已加入该社团"),
    ACTIVITY_FULL(1005, "活动名额已满"),
    OUT_OF_CHECKIN_RANGE(1006, "不在打卡范围内"),
    ALREADY_CHECKED_IN(1007, "已打卡");

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}