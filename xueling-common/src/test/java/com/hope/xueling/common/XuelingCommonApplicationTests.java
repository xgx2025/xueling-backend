package com.hope.xueling.common;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class XuelingCommonApplicationTests {

    @Test
    void contextLoads() {

    }

    /**
     * 打印doubao的api-key
     */
    @Test
    void printDoubaoApiKey() {
        System.out.println("doubao api-key: " + System.getenv("DOUBAO_API_KEY"));
    }

}
