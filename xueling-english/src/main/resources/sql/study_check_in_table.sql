CREATE TABLE IF NOT EXISTS `study_check_in` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `user_id` bigint unsigned NOT NULL,
    `study_date` date NOT NULL COMMENT '按用户学习时区计算的日期',
    `checked_in_at` datetime(3) NOT NULL COMMENT 'UTC完成时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_study_date` (`user_id`, `study_date`),
    CONSTRAINT `fk_study_check_in_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日记忆任务自动打卡';
