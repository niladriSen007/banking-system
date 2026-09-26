CREATE TABLE IF NOT EXISTS outbox_events
(
    bucketdate      date,
    event_time      timestamp,
    event_id        uuid,
    aggregate_id    uuid,
    event_type      text,
    payload         text,
    status          text,
    retry_count     int,
    last_attempt_at timestamp,
    published_at    timestamp,
    last_error      text,

    PRIMARY KEY ((bucketdate), event_time, event_id)
)
            WITH CLUSTERING ORDER BY (event_time ASC);