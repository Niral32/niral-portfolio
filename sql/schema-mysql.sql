-- Portfolio database schema (MySQL 8+)
-- Run: mysql -u root -p portfolio_db < schema-mysql.sql

CREATE TABLE IF NOT EXISTS projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    tech_stack VARCHAR(500) NOT NULL,
    link_url VARCHAR(500) NULL,
    display_order INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    category VARCHAR(32) NOT NULL,
    display_order INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS work_experience (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_title VARCHAR(200) NOT NULL,
    organization VARCHAR(200) NOT NULL,
    summary VARCHAR(4000) NOT NULL,
    start_period VARCHAR(64),
    end_period VARCHAR(64),
    display_order INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS contact_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(254) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    message VARCHAR(4000) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    read_at DATETIME(6) NULL
);

CREATE TABLE IF NOT EXISTS resume (
    id BIGINT PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    data LONGBLOB NOT NULL,
    uploaded_at DATETIME(6) NOT NULL
);

CREATE INDEX idx_skills_category ON skills (category);
CREATE INDEX idx_contact_created ON contact_messages (created_at DESC);
