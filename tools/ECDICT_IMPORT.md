# ECDICT 导入

词库来源：[skywind3000/ECDICT](https://github.com/skywind3000/ECDICT)。本次使用提交
`bc015ed2e24a7abef49fc6dbbb7fe32c1dadaf8b` 的 `ecdict.csv`。词库按上游
[MIT LICENSE](https://github.com/skywind3000/ECDICT/blob/master/LICENSE) 使用；再分发时保留版权和许可声明。

1. 备份 MySQL。已有数据库先执行 `xueling-english/src/main/resources/sql/migrate_word_dictionary_ecdict.sql`；新数据库使用 `word_dictionary_table.sql`。
2. 从上述固定提交下载 `ecdict.csv` 到 `data/ecdict.csv`。`data/` 已在 `.gitignore` 中。
3. 设置 `MYSQL_PWD`（或配置 MySQL login path），然后运行：

   ```powershell
   python tools/import_ecdict.py --csv data/ecdict.csv --version bc015ed2e24a7abef49fc6dbbb7fe32c1dadaf8b --dry-run
   python tools/import_ecdict.py --csv data/ecdict.csv --version bc015ed2e24a7abef49fc6dbbb7fe32c1dadaf8b --database xueling_db --user <mysql-user>
   ```

导入按 `word_key` 更新，保留原词条 `id`，可以重跑。`*.rejects.tsv` 记录未导入行；
空中文释义的词条仍导入，查询时由 AI 补充。导入不会自动删除旧版本中已不存在的词条。
