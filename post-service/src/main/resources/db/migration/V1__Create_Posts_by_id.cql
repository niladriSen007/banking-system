CREATE TABLE IF NOT EXISTS post_service.posts_by_id
(
    post_id       UUID PRIMARY KEY,
    user_id       UUID,
    content       TEXT,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    like_count    BIGINT,
    comment_count BIGINT
);