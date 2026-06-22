package com.clubmanage.security;

import com.clubmanage.common.BusinessException;
import com.clubmanage.common.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static LoginUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof LoginUser loginUser)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return loginUser;
    }

    public static Long currentUserId() {
        return currentUser().getUserId();
    }

    public static boolean isAdmin() {
        return currentUser().getUser().getRole() != null && currentUser().getUser().getRole() == 2;
    }
}