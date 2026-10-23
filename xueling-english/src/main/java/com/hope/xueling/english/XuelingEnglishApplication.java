package com.hope.xueling.english;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {"com.hope.xueling"}
)
@MapperScan({"com.hope.xueling.english.vocab.mapper", "com.hope.xueling.english.reading.mapper", "com.hope.xueling.common.mapper"})
public class XuelingEnglishApplication {

    public static void main(String[] args) {
        SpringApplication.run(XuelingEnglishApplication.class, args);
    }
    static {
        // 在应用启动前设置Jackson系统属性
        System.setProperty("jackson.deserialization.fail-on-unknown-properties", "false");
        System.setProperty("spring.jackson.deserialization.fail-on-unknown-properties", "false");
    }
}
