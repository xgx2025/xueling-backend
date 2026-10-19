package com.hope.xueling.english.vocab.service;

import com.hope.xueling.english.vocab.domain.dto.AddWordsToWordBookDTO;
import com.hope.xueling.english.vocab.domain.dto.CreateWordBookDTO;
import com.hope.xueling.english.vocab.domain.dto.RemoveWordsFromWordBookDTO;
import com.hope.xueling.english.vocab.domain.vo.WordBookDetailVO;
import com.hope.xueling.english.vocab.domain.vo.WordBookVO;
import com.hope.xueling.english.vocab.domain.vo.WordDictionaryVO;
import java.util.List;

/**
 * 单词本服务接口
 * @author 谢光湘
 * @since 2026/1/26
 */
public interface IWordBookService {
    /**
     * 创建一个单词本
     * @param createWordBookDTO 单词本DTO
     */
    void createWordBook(CreateWordBookDTO createWordBookDTO, Long userId);

    /**
     * 获取用户所有单词本
     * @return 用户所有单词本列表
     */
    List<WordBookVO> getWordBooks(Long userId);
    /**
     * 获取单词本详情
     * @param wordBookId 单词本ID
     * @return 单词本详情
     */
    WordBookDetailVO getWordBookDetail(Long wordBookId, Long userId);
    /**
     * 匹配用户输入的单词是否参在于系统的单词库中
     * @param words 用户输入的单词列表
     * @return 匹配到的单词列表
     */
    List<WordDictionaryVO> matchWords(String words);

    /**
     * 将用户输入的单词添加到单词本
     * @param addWordsToWordBookDTO 添加单词到单词本DTO
     * @param userId 用户ID
     */
    void addWordsToWordBook(AddWordsToWordBookDTO addWordsToWordBookDTO, Long userId);

    /**
     * 从单词本中删除单词
     * @param removeWordsFromWordBookDTO 删除单词从单词本DTO
     * @param userId 用户ID
     */
    void deleteWordsFromWordBook(RemoveWordsFromWordBookDTO removeWordsFromWordBookDTO, Long userId);
}
