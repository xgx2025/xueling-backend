package com.hope.xueling.english;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 英语学习服务启动类：扫描 com.hope.xueling 全包，并注册词书、阅读与公共三个 mapper 包；启动前关闭 Jackson 未知字段报错
 * @author 谢光湘
 * @since 2026/10/24
 */
@SpringBootApplication(
        scanBasePackages = {"com.hope.xueling"}
)
@MapperScan({"com.hope.xueling.english.vocab.mapper", "com.hope.xueling.english.reading.mapper", "com.hope.xueling.common.mapper"})
public class XuelingEnglishApplication {

    /**
     * 启动 Spring Boot 应用
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(XuelingEnglishApplication.class, args);
    }
    static {
        // 在应用启动前设置Jackson系统属性
        System.setProperty("jackson.deserialization.fail-on-unknown-properties", "false");
        System.setProperty("spring.jackson.deserialization.fail-on-unknown-properties", "false");
    }
}
