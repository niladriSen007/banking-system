CREATE TABLE IF NOT EXISTS post_service.posts_by_user
(
    user_id       UUID,
    created_at    TIMESTAMP,
    post_id       UUID,
    content       TEXT,
    updated_at    TIMESTAMP,
    like_count    BIGINT,
    comment_count BIGINT,
    PRIMARY KEY ((user_id), created_at, post_id)
)
            WITH CLUSTERING ORDER BY (created_at DESC);