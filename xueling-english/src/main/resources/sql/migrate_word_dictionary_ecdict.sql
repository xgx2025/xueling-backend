-- 在已有 word_dictionary 上执行一次。运行前备份数据库。
-- 如果原有数据在忽略大小写后有重复词条，先人工合并，再创建唯一索引。
ALTER TABLE `word_dictionary`
    MODIFY COLUMN `word` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    MODIFY COLUMN `meaning` mediumtext COLLATE utf8mb4_unicode_ci NULL,
    MODIFY COLUMN `phonetic` varchar(255) COLLATE utf8mb4_unicode_ci NULL,
    MODIFY COLUMN `example` varchar(500) COLLATE utf8mb4_unicode_ci NULL,
    MODIFY COLUMN `example_translation` varchar(500) COLLATE utf8mb4_unicode_ci NULL,
    ADD COLUMN `word_key` varchar(255) COLLATE utf8mb4_bin NULL AFTER `word`,
    ADD COLUMN `definition` mediumtext COLLATE utf8mb4_unicode_ci NULL AFTER `phonetic`,
    ADD COLUMN `pos` varchar(255) COLLATE utf8mb4_unicode_ci NULL AFTER `definition`,
    ADD COLUMN `collins` tinyint unsigned NULL AFTER `pos`,
    ADD COLUMN `oxford` tinyint unsigned NULL AFTER `collins`,
    ADD COLUMN `tag` text COLLATE utf8mb4_unicode_ci NULL AFTER `oxford`,
    ADD COLUMN `bnc` int unsigned NULL AFTER `tag`,
    ADD COLUMN `frq` int unsigned NULL AFTER `bnc`,
    ADD COLUMN `exchange` text COLLATE utf8mb4_unicode_ci NULL AFTER `frq`,
    ADD COLUMN `detail` mediumtext COLLATE utf8mb4_unicode_ci NULL AFTER `exchange`,
    ADD COLUMN `audio` varchar(1000) COLLATE utf8mb4_unicode_ci NULL AFTER `detail`,
    ADD COLUMN `source` varchar(20) COLLATE utf8mb4_unicode_ci NULL AFTER `audio`,
    ADD COLUMN `dataset_version` varchar(64) COLLATE utf8mb4_unicode_ci NULL AFTER `source`;

UPDATE `word_dictionary`
SET `word_key` = LOWER(TRIM(`word`)), `source` = 'AI'
WHERE `word_key` IS NULL;

ALTER TABLE `word_dictionary`
    MODIFY COLUMN `word_key` varchar(255) COLLATE utf8mb4_bin NOT NULL,
    MODIFY COLUMN `source` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
    DROP INDEX `uk_word`,
    ADD UNIQUE KEY `uk_word_key` (`word_key`);
