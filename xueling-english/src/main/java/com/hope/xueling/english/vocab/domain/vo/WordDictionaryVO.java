package com.hope.xueling.english.vocab.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 单词字典视图对象
 * @author 谢光湘
 * @since 2026/1/26
 */
@Data
@NoArgsConstructor
public class WordDictionaryVO {
    /**
     * 主键ID
     */
    private String id;
    /**
     * 单词
     */
    private String word;
    /**
     * 释义
     */
    private String meaning;
    /**
     * 按词性分组的释义列表
     */
    private List<WordTranslationVO> translations;
    /**
     * 词条来源，如 ECDICT、ADMIN_IMPORT
     */
    private String source;
    /**
     * 音标
     */
    private String phonetic;
    /**
     * 英文例句
     */
    private String example;
    /**
     * 例句中文翻译
     */
    private String exampleTranslation;
     /**
     * 是否在单词本中
     */
    private Boolean inWordBook;
    /**
     * 单词本ID
     */
    private String wordBookId;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
