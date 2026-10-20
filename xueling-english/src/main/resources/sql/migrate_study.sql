-- 开发环境迁移：旧进度与评分表没有真实数据；词典数据保留。
DROP TABLE IF EXISTS `word_review_record`;
DROP TABLE IF EXISTS `word_learning_progress`;
ALTER TABLE `word_book`
    MODIFY `user_id` bigint unsigned NULL,
    ADD COLUMN `book_type` varchar(12) NOT NULL DEFAULT 'USER' AFTER `user_id`,
    ADD COLUMN `system_key` varchar(40) NULL AFTER `book_type`,
    ADD UNIQUE KEY `uk_system_key` (`system_key`),
    ADD KEY `idx_type_deleted` (`book_type`, `is_deleted`);
ALTER TABLE `word_book_dictionary_relation`
    ADD COLUMN `sort_order` int unsigned NOT NULL DEFAULT 0 AFTER `word_id`,
    ADD KEY `idx_book_order` (`word_book_id`, `sort_order`, `word_id`);
-- 接着执行四张新表的结构文件，最后执行 seed_cet4.sql。
