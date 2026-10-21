# 单词学习功能数据库准备

数据库使用 MySQL 8.0。学习时间字段按 UTC 写入 `datetime(3)`；用户设置中的时区仅用于划分“今天”和展示时间。

## 已有开发数据库

旧的 `word_learning_progress` 和 `word_review_record` 没有真实数据时，依次执行：

1. `migrate_study.sql`：删除旧的空学习表并扩展词书结构。
2. `user_study_settings_table.sql`
3. `user_study_book_table.sql`
4. `word_learning_progress_table.sql`
5. `word_review_record_table.sql`
6. `study_check_in_table.sql`：创建每日自动打卡表（已有学习数据库也需单独执行）。
7. `seed_cet4.sql`：从已导入的 ECDICT 建立系统四级词书。

如果 ECDICT 尚未导入，先完成词典导入，再执行 `seed_cet4.sql`。`migrate_study.sql` 只执行一次。MySQL 命令行执行 SQL 文件时显式指定 `--default-character-set=utf8mb4`。

## 新数据库

先按原项目顺序创建 `user`、`word_dictionary`、`word_book` 和 `word_book_dictionary_relation`，接着执行上面的第 2～6 个表结构文件。导入词典后执行 `seed_cet4.sql`。

系统词书通过 `system_key='CET4'` 查询，不依赖固定主键。用户首次访问 `/study/today` 时自动创建默认学习设置，并将系统四级词书加入自己的学习计划。
