package com.hope.xueling.english;

import cn.hutool.core.util.ReUtil;
import com.hope.xueling.english.vocab.domain.vo.WordFamilyNodeVO;
import com.hope.xueling.english.vocab.service.IWordReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class XuelingEnglishApplicationTests {
    @Autowired
    private IWordReviewService wordReviewService;



    @Test
    void contextLoads() {
        String str = "Hello我是谁？";
        String str1 = String.join("", ReUtil.findAll("[\\u4e00-\\u9fa5]", str, 0));
        System.out.println(str1);
        String str2 = String.join("",ReUtil.findAll("[a-zA-Z]", str, 0));
        System.out.println(str2);
    }

    @Test
    void testGetWordFamilyTree() {
        String baseWord = "happy";
        WordFamilyNodeVO root = wordReviewService.getWordFamilyTree(baseWord);
        System.out.println(root);
    }
}
