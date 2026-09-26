CREATE TABLE IF NOT EXISTS post_service.recent_posts
(
    bucket_date   DATE,
    created_at    TIMESTAMP,
    post_id       UUID,
    user_id       UUID,
    content       TEXT,
    updated_at    TIMESTAMP,
    like_count    BIGINT,
    comment_count BIGINT,
    PRIMARY KEY ((bucket_date), created_at, post_id)
)
            WITH CLUSTERING ORDER BY (created_at DESC);