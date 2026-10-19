package com.hope.xueling.english.vocab.service;

import com.hope.xueling.english.vocab.domain.dto.WordDictionaryDTO;

/**
 * 生成词典结构化释义的服务接口
 * @author 谢光湘
 * @since 2026/1/26
 */
public interface IWordDefinitionService {
    /**
     * 获取单词的释义、音标和例句
     * @param word 单词
     * @return 词典条目
     */
    WordDictionaryDTO translateWord(String word);
}
