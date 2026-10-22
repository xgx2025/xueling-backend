# 意见反馈模块接入

1. 在应用使用的 MySQL 数据库执行 `user_feedback_table.sql`。该脚本只新增 `user_feedback` 表，可重复执行。
2. 设置环境变量 `XUELING_FEEDBACK_ADMIN_IDS` 为可处理反馈的用户 ID，多个 ID 以英文逗号分隔。也可在 `application.yml` 写入 `xueling.feedback.admin-user-ids`。不配置时，无人拥有反馈处理权限。
3. 重启后端。用户登录后可调用 `/feedback` 提交反馈，并通过 `/feedback/mine` 查看记录；白名单用户可通过 `/feedback/admin` 处理。

前端入口位于首页账户菜单和设置页。翻译抽屉内的“内容有误”也使用同一接口。数据库表不会由应用启动时自动创建，部署前需执行第 1 步。
