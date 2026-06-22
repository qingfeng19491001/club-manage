package com.clubmanage.dto.auth;

import com.clubmanage.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileVO {

    private Long id;
    private String username;
    private String realName;
    private String studentNo;
    private String phone;
    private String email;
    private String avatarUrl;
    private Integer role;

    public static UserProfileVO from(User user) {
        return UserProfileVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .studentNo(user.getStudentNo())
                .phone(user.getPhone())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .build();
    }
}