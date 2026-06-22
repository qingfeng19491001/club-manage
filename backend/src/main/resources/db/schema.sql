CREATE DATABASE IF NOT EXISTS club_manage DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE club_manage;

-- users: role 0=member 1=leader 2=admin
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    real_name VARCHAR(64),
    student_no VARCHAR(32),
    phone VARCHAR(20),
    email VARCHAR(128),
    avatar_url VARCHAR(512),
    role TINYINT NOT NULL DEFAULT 0 COMMENT '0 member 1 leader 2 admin',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0 disabled 1 active',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB;

-- clubs: status 0=pending 1=approved 2=rejected 3=disbanded
CREATE TABLE IF NOT EXISTS clubs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL UNIQUE,
    description TEXT,
    logo_url VARCHAR(512),
    founder_id BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    reject_reason VARCHAR(512),
    member_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_clubs_status (status),
    INDEX idx_clubs_founder (founder_id)
) ENGINE=InnoDB;

-- members: role 0=member 1=vice_leader 2=leader; status 0=pending 1=active 2=rejected 3=left
CREATE TABLE IF NOT EXISTS members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    club_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role TINYINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 0,
    apply_reason VARCHAR(512),
    reject_reason VARCHAR(512),
    joined_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_club_user (club_id, user_id),
    INDEX idx_members_club (club_id),
    INDEX idx_members_user (user_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS activities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    club_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    description TEXT,
    location VARCHAR(256),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    max_participants INT NOT NULL DEFAULT 0 COMMENT '0 unlimited',
    registered_count INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0 draft 1 published 2 ended 3 cancelled',
    cover_url VARCHAR(512),
    recap TEXT,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_activities_club (club_id),
    INDEX idx_activities_start (start_time)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS registrations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1 registered 2 cancelled 3 checked_in',
    checked_in_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_activity_user (activity_id, user_id),
    INDEX idx_reg_activity (activity_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS checkin_tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
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
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_checkin_tasks_club (club_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS checkin_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1 success 2 appeal pending 3 appeal approved 4 appeal rejected',
    appeal_reason VARCHAR(512),
    appeal_reply VARCHAR(512),
    checked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_task_user (task_id, user_id),
    INDEX idx_checkin_records_task (task_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS funds (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    club_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    type TINYINT NOT NULL COMMENT '1 income 2 expense',
    description TEXT,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0 pending 1 approved 2 rejected',
    applicant_id BIGINT NOT NULL,
    approver_id BIGINT,
    reject_reason VARCHAR(512),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_funds_club (club_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS announcements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    club_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    content TEXT NOT NULL,
    is_pinned TINYINT NOT NULL DEFAULT 0,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_announcements_club (club_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    content TEXT NOT NULL,
    type TINYINT NOT NULL DEFAULT 0 COMMENT '0 system 1 club 2 activity',
    ref_id BIGINT,
    is_read TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_messages_user (user_id, is_read)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS files (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    uploader_id BIGINT NOT NULL,
    original_name VARCHAR(256) NOT NULL,
    storage_path VARCHAR(512) NOT NULL,
    mime_type VARCHAR(128),
    size_bytes BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS system_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(128) NOT NULL UNIQUE,
    config_value TEXT,
    description VARCHAR(256),
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO users (username, password_hash, real_name, role, status)
SELECT 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '系统管理员', 2, 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');
-- default password: password123