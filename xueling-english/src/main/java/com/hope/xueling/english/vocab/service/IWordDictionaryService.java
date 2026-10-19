package com.hope.xueling.english.vocab.service;

import com.hope.xueling.english.vocab.domain.dto.WordDictionaryDTO;
import com.hope.xueling.english.vocab.domain.vo.WordDictionaryVO;
import com.hope.xueling.english.vocab.domain.vo.WordVO;

import java.util.List;

/**
 * 单词字典服务接口
 * @author 谢光湘
 * @since 2026/1/25
 */
public interface IWordDictionaryService {
    /**
     * 查询单词
     * @param word 英语单词
     * @return 单词字典
     */
    WordDictionaryVO queryWordDictionary(String word);
    /**
     * 通过wordIds获取单词详情
     * @param wordIds 单词ID列表
     * @return 单词详情列表
     */
    List<WordVO> getWordDetailByIds(List<String> wordIds);

    /**
     * 保存单词字典
     * @param wordDictionary 单词字典
     */
    void saveWordDictionary(Long id,WordDictionaryDTO wordDictionary);
}
