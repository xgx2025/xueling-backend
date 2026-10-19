package com.hope.xueling.english.vocab.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 单词字典实体类
 * @author 谢光湘
 * @since 2026/1/25
 */
@Data
@NoArgsConstructor
public class WordDictionary {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 单词
     */
    private String word;
    /**
     * 释义
     */
    private String meaning;
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
      * 单词插图URL
      */
    private String imageUrl;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
