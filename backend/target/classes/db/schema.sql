-- users: role 0=member 1=leader 2=admin
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    real_name VARCHAR(64),
    student_no VARCHAR(32),
    phone VARCHAR(20),
    email VARCHAR(128),
    avatar_url VARCHAR(512),
    role INTEGER NOT NULL DEFAULT 0,
    status INTEGER NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0
);

-- clubs: status 0=pending 1=approved 2=rejected 3=disbanded
CREATE TABLE IF NOT EXISTS clubs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(128) NOT NULL UNIQUE,
    description TEXT,
    logo_url VARCHAR(512),
    founder_id BIGINT NOT NULL,
    status INTEGER NOT NULL DEFAULT 0,
    reject_reason VARCHAR(512),
    member_count INTEGER NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_clubs_status ON clubs (status);
CREATE INDEX IF NOT EXISTS idx_clubs_founder ON clubs (founder_id);

-- members: role 0=member 1=vice_leader 2=leader; status 0=pending 1=active 2=rejected 3=left
CREATE TABLE IF NOT EXISTS members (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    club_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role INTEGER NOT NULL DEFAULT 0,
    status INTEGER NOT NULL DEFAULT 0,
    apply_reason VARCHAR(512),
    reject_reason VARCHAR(512),
    joined_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    UNIQUE (club_id, user_id)
);
CREATE INDEX IF NOT EXISTS idx_members_club ON members (club_id);
CREATE INDEX IF NOT EXISTS idx_members_user ON members (user_id);

CREATE TABLE IF NOT EXISTS activities (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    club_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    description TEXT,
    location VARCHAR(256),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    max_participants INTEGER NOT NULL DEFAULT 0,
    registered_count INTEGER NOT NULL DEFAULT 0,
    status INTEGER NOT NULL DEFAULT 1,
    cover_url VARCHAR(512),
    recap TEXT,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_activities_club ON activities (club_id);
CREATE INDEX IF NOT EXISTS idx_activities_start ON activities (start_time);

CREATE TABLE IF NOT EXISTS registrations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status INTEGER NOT NULL DEFAULT 1,
    checked_in_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    UNIQUE (activity_id, user_id)
);
CREATE INDEX IF NOT EXISTS idx_reg_activity ON registrations (activity_id);

CREATE TABLE IF NOT EXISTS checkin_tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    club_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    description TEXT,
    location_name VARCHAR(256),
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    radius_meters INTEGER NOT NULL DEFAULT 200,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_checkin_tasks_club ON checkin_tasks (club_id);

CREATE TABLE IF NOT EXISTS checkin_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    status INTEGER NOT NULL DEFAULT 1,
    appeal_reason VARCHAR(512),
    appeal_reply VARCHAR(512),
    checked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    UNIQUE (task_id, user_id)
);
CREATE INDEX IF NOT EXISTS idx_checkin_records_task ON checkin_records (task_id);

CREATE TABLE IF NOT EXISTS funds (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    club_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    type INTEGER NOT NULL,
    description TEXT,
    status INTEGER NOT NULL DEFAULT 0,
    applicant_id BIGINT NOT NULL,
    approver_id BIGINT,
    reject_reason VARCHAR(512),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_funds_club ON funds (club_id);

CREATE TABLE IF NOT EXISTS announcements (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    club_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    content TEXT NOT NULL,
    is_pinned INTEGER NOT NULL DEFAULT 0,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_announcements_club ON announcements (club_id);

CREATE TABLE IF NOT EXISTS messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    content TEXT NOT NULL,
    type INTEGER NOT NULL DEFAULT 0,
    ref_id BIGINT,
    is_read INTEGER NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_messages_user ON messages (user_id, is_read);

CREATE TABLE IF NOT EXISTS files (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    uploader_id BIGINT NOT NULL,
    original_name VARCHAR(256) NOT NULL,
    storage_path VARCHAR(512) NOT NULL,
    mime_type VARCHAR(128),
    size_bytes BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS system_configs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    config_key VARCHAR(128) NOT NULL UNIQUE,
    config_value TEXT,
    description VARCHAR(256),
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT OR IGNORE INTO users (username, password_hash, real_name, role, status)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '系统管理员', 2, 1);
