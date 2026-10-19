package com.hope.xueling.english.vocab.service;

import com.hope.xueling.english.vocab.domain.vo.WordFamilyNodeVO;

/**
 * 单词复习服务接口
 * @author 谢光湘
 * @since 2026/1/28
 */
public interface IWordReviewService {

    /**
     * 获取单词家族树
     * @param baseWord 基础单词（如"beautiful"）
     * @return 单词家族树视图对象
     */
    WordFamilyNodeVO getWordFamilyTree(String baseWord);

}
