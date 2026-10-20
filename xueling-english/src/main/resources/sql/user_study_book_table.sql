CREATE TABLE `user_study_book` (
    `user_id` bigint unsigned NOT NULL,
    `book_id` bigint unsigned NOT NULL,
    `enabled` tinyint(1) NOT NULL DEFAULT 1,
    `priority` int unsigned NOT NULL DEFAULT 100,
    PRIMARY KEY (`user_id`, `book_id`),
    KEY `idx_user_enabled` (`user_id`, `enabled`, `priority`),
    CONSTRAINT `fk_study_book_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_study_book_book` FOREIGN KEY (`book_id`) REFERENCES `word_book` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
