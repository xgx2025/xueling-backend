# 学灵 (XueLing) - 智能学习平台

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)](https://maven.apache.org/)

一个辅助学生高效学习的智能学习网站，利用人工智能技术提供个性化的学习体验。

## 📖 项目简介

**学灵 (XueLing)** 是一个现代化的智能学习平台，旨在帮助学生更高效地学习。项目采用前后端分离架构（后端），集成了 AI 大模型能力，为学生提供智能化的学习辅助。

## 🏗️ 项目结构

```
学灵 (xueling)/
├── xueling-common/          # 公共模块 - 包含通用工具类、常量定义等
├── xueling-english/         # 英语学习模块 - 英语单词记忆学习功能
├── pom.xml                  # 父工程 Maven 配置
├── mvnw                     # Maven Wrapper 脚本 (Unix)
└── mvnw.cmd                 # Maven Wrapper 脚本 (Windows)
```

## 🛠️ 技术栈

### 核心框架
- **Spring Boot 3.5.9** - 应用开发框架
- **Java 21** - 编程语言
- **Maven** - 项目构建与依赖管理

### 数据存储
- **MySQL** - 关系型数据库
- **Redis** - 缓存数据库
- **MyBatis-Plus 3.5.12** - ORM 框架
- **MyBatis-Plus-Join 1.5.4** - 多表联查扩展
- **Druid 1.2.21** - 数据库连接池

### AI 与大模型
- **Spring AI 1.1.2** - Spring 官方 AI 框架
- **LangChain4j 1.0.0-beta3** - Java 版 LangChain 框架
- **OpenAI API** - 大语言模型服务

### 安全与认证
- **Spring Security** - 安全框架
- **JWT (JJWT 0.9.1)** - Token 认证

### 工具与服务
- **Hutool 5.8.40** - Java 工具库
- **Lombok** - 代码简化库
- **阿里云 OSS 3.17.4** - 对象存储服务
- **Apache Commons Email 1.5** - 邮件服务

### 测试
- **JUnit 5** - 单元测试框架
- **Spring Boot Test** - 集成测试支持

## 🚀 快速开始

### 环境要求
- JDK 21+
- Maven 3.8+ 或使用项目自带的 Maven Wrapper
- MySQL 8.0+
- Redis 6.0+

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd 学灵
```

2. **配置数据库**
创建 MySQL 数据库并修改配置文件中的数据库连接信息。

3. **配置文件**
在 `xueling-english/src/main/resources/` 目录下创建或修改 `application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xueling?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
  ai:
    openai:
      api-key: your-openai-api-key
```

4. **构建项目**
```bash
# Unix/Linux/Mac
./mvnw clean install

# Windows
mvnw.cmd clean install
```

5. **运行项目**
```bash
# 运行英语学习模块
cd xueling-english
../mvnw spring-boot:run
```

## 📋 功能特性

### 已实现功能
- ✅ 用户注册、登录功能
- ✅ JWT Token 认证
- ✅ 英语单词记忆学习模块

### 开发中功能
- 🔄 AI 智能学习推荐
- 🔄 学习进度追踪
- 🔄 个性化学习计划

### 计划功能
- 📌 多科目支持（数学、物理、化学等）
- 📌 错题本功能
- 📌 学习数据分析报表
- 📌 移动端适配

## 🔧 模块说明

### xueling-common
公共模块，包含：
- 通用工具类
- 常量定义
- 统一响应封装
- 自定义异常处理

### xueling-english
英语学习模块，包含：
- 单词管理
- 记忆曲线算法
- AI 辅助学习
- 学习记录追踪

## 📝 API 文档

项目启动后，访问以下地址查看 API 文档（如配置了 Swagger/OpenAPI）：
```
http://localhost:8080/swagger-ui.html
```

## 🤝 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📄 许可证

本项目采用 [MIT 许可证](LICENSE)

## 👥 开发团队

- 开发者：Hope
- 联系方式：[your-email@example.com]

## 🙏 致谢

感谢以下开源项目：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Plus](https://baomidou.com/)
- [Spring AI](https://spring.io/projects/spring-ai)
- [LangChain4j](https://github.com/langchain4j/langchain4j)
- [Hutool](https://hutool.cn/)

---

**Made with ❤️ for students everywhere**