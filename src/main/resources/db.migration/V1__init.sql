CREATE TABLE users
(
    user_id                           BIGINT PRIMARY KEY,
    chat_id                           VARCHAR(255) NOT NULL,
    user_name                         VARCHAR(255),
    first_name                        VARCHAR(255),
    last_name                         VARCHAR(255),
    should_send_push_up_notifications BOOLEAN DEFAULT FALSE,
    should_send_pull_up_notifications BOOLEAN DEFAULT FALSE,
    push_up_day_goal                  INT,
    push_up_week_goal                 INT,
    push_up_month_goal                INT,
    pull_up_day_goal                  INT,
    pull_up_week_goal                 INT,
    pull_up_month_goal                INT
);

CREATE TABLE exercise
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT                   NOT NULL,
    exercise_type VARCHAR(50)              NOT NULL,
    count         INT                      NOT NULL,
    date_time     TIMESTAMP WITH TIME ZONE NOT NULL
);
