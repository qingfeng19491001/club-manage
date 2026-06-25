-- 社团管理系统 MySQL 建表脚本
-- MySQL 8.0+

-- users: role 0=member 1=leader 2=admin
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    real_name VARCHAR(64),
    student_no VARCHAR(32),
    phone VARCHAR(20),
    email VARCHAR(128),
    avatar_url VARCHAR(512),
    role INT NOT NULL DEFAULT 0 COMMENT '0=member 1=leader 2=admin',
    status INT NOT NULL DEFAULT 1 COMMENT '1=active 0=inactive',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- clubs: status 0=pending 1=approved 2=rejected 3=disbanded
CREATE TABLE IF NOT EXISTS clubs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(64) NOT NULL DEFAULT '其他',
    logo_url VARCHAR(512),
    founder_id BIGINT NOT NULL,
    status INT NOT NULL DEFAULT 0 COMMENT '0=pending 1=approved 2=rejected 3=disbanded',
    reject_reason VARCHAR(512),
    member_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    INDEX idx_status (status),
    INDEX idx_founder (founder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- members: role 0=member 1=vice_leader 2=leader; status 0=pending 1=active 2=rejected 3=left
CREATE TABLE IF NOT EXISTS members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role INT NOT NULL DEFAULT 0 COMMENT '0=member 1=vice_leader 2=leader',
    status INT NOT NULL DEFAULT 0 COMMENT '0=pending 1=active 2=rejected 3=left',
    apply_reason VARCHAR(512),
    reject_reason VARCHAR(512),
    joined_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_club_user (club_id, user_id),
    INDEX idx_club (club_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    description TEXT,
    location VARCHAR(256),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    max_participants INT NOT NULL DEFAULT 0,
    registered_count INT NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 1 COMMENT '1=upcoming 2=ongoing 3=ended 0=cancelled',
    cover_url VARCHAR(512),
    recap TEXT,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    INDEX idx_club (club_id),
    INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status INT NOT NULL DEFAULT 1 COMMENT '1=registered 2=cancelled 3=checked_in',
    checked_in_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_activity_user (activity_id, user_id),
    INDEX idx_activity (activity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS checkin_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    description TEXT,
    location_name VARCHAR(256),
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    radius_meters INT NOT NULL DEFAULT 200,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    INDEX idx_club (club_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS checkin_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    status INT NOT NULL DEFAULT 1 COMMENT '1=normal 2=abnormal 3=appealed 4=approved',
    appeal_reason VARCHAR(512),
    appeal_reply VARCHAR(512),
    checked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_task_user (task_id, user_id),
    INDEX idx_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS funds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    type INT NOT NULL COMMENT '0=expense 1=income',
    description TEXT,
    status INT NOT NULL DEFAULT 0 COMMENT '0=pending 1=approved 2=rejected',
    applicant_id BIGINT NOT NULL,
    approver_id BIGINT,
    reject_reason VARCHAR(512),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    INDEX idx_club (club_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS announcements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    content TEXT NOT NULL,
    is_pinned INT NOT NULL DEFAULT 0,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    INDEX idx_club (club_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    content TEXT NOT NULL,
    type INT NOT NULL DEFAULT 0 COMMENT '0=system 1=activity 2=checkin 3=club',
    ref_id BIGINT,
    is_read INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    INDEX idx_user_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uploader_id BIGINT NOT NULL,
    original_name VARCHAR(256) NOT NULL,
    storage_path VARCHAR(512) NOT NULL,
    mime_type VARCHAR(128),
    size_bytes BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS system_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(128) NOT NULL UNIQUE,
    config_value TEXT,
    description VARCHAR(256),
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 初始化管理员账号 (密码: admin123)
INSERT IGNORE INTO users (username, password_hash, real_name, role, status)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '系统管理员', 2, 1);
