CREATE TABLE `user_study_settings` (
    `user_id` bigint unsigned NOT NULL,
    `time_zone` varchar(64) NOT NULL DEFAULT 'Asia/Shanghai',
    `daily_new_limit` tinyint unsigned NOT NULL DEFAULT 15,
    `desired_retention` decimal(4,3) NOT NULL DEFAULT 0.900,
    `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (`user_id`),
    CONSTRAINT `fk_study_settings_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
