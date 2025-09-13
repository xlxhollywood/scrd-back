-- H2 데이터베이스 초기화 스크립트
-- 테스트용 테이블 생성

-- Users 테이블 (H2에서 user는 예약어이므로 users로 생성)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    kakao_id BIGINT,
    apple_id VARCHAR(255),
    name VARCHAR(255),
    nick_name VARCHAR(255),
    email VARCHAR(255),
    profile_image_url VARCHAR(500),
    birth VARCHAR(10),
    gender VARCHAR(10),
    point INTEGER DEFAULT 0,
    count INTEGER DEFAULT 0,
    tier VARCHAR(20) DEFAULT 'ONE',
    role VARCHAR(20) DEFAULT 'ROLE_USER',
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Theme 테이블
CREATE TABLE IF NOT EXISTS theme (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    location VARCHAR(255),
    price INTEGER,
    image TEXT,
    url VARCHAR(500),
    brand VARCHAR(255),
    branch VARCHAR(255),
    playtime INTEGER,
    proportion VARCHAR(50),
    rating FLOAT,
    horror INTEGER,
    activity INTEGER,
    level FLOAT,
    review_count INTEGER DEFAULT 0,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tag 테이블
CREATE TABLE IF NOT EXISTS tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(255),
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Review 테이블
CREATE TABLE IF NOT EXISTS review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    text TEXT,
    level INTEGER,
    stars INTEGER,
    horror INTEGER,
    activity INTEGER,
    is_successful BOOLEAN,
    hint_usage_count INTEGER,
    clear_time VARCHAR(50),
    user_id BIGINT,
    theme_id BIGINT,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (theme_id) REFERENCES theme(id)
);

-- ReviewTagMap 테이블
CREATE TABLE IF NOT EXISTS review_tag_map (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id BIGINT,
    tag_id BIGINT,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (review_id) REFERENCES review(id),
    FOREIGN KEY (tag_id) REFERENCES tag(id)
);

-- SavedTheme 테이블
CREATE TABLE IF NOT EXISTS saved_theme (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    theme_id BIGINT,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (theme_id) REFERENCES theme(id)
);

-- PartyPost 테이블
CREATE TABLE IF NOT EXISTS party_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    content TEXT,
    theme_id BIGINT,
    writer_id BIGINT,
    max_participants INTEGER,
    current_participants INTEGER DEFAULT 0,
    deadline TIMESTAMP,
    is_closed BOOLEAN DEFAULT FALSE,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (theme_id) REFERENCES theme(id),
    FOREIGN KEY (writer_id) REFERENCES users(id)
);

-- PartyJoin 테이블
CREATE TABLE IF NOT EXISTS party_join (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    party_post_id BIGINT,
    user_id BIGINT,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (party_post_id) REFERENCES party_post(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- PartyComment 테이블
CREATE TABLE IF NOT EXISTS party_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT,
    party_post_id BIGINT,
    user_id BIGINT,
    parent_id BIGINT,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (party_post_id) REFERENCES party_post(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (parent_id) REFERENCES party_comment(id)
);

-- Notification 테이블
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receiver_id BIGINT NOT NULL,
    sender_id BIGINT,
    type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    related_post_id BIGINT,
    related_comment_id BIGINT,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (receiver_id) REFERENCES users(id),
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (related_post_id) REFERENCES party_post(id),
    FOREIGN KEY (related_comment_id) REFERENCES party_comment(id)
);

-- RefreshToken 테이블
CREATE TABLE IF NOT EXISTS refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    token VARCHAR(500),
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
