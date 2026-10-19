package com.hope.xueling.english.vocab.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 单词字典视图对象
 * @author 谢光湘
 * @since 2026/1/26
 */
@Data
@NoArgsConstructor
public class WordVO {
    /**
     * 单词ID
     */
    private String id;
    /**
     * 单词
     */
    private String word;
    /**
     * 单词含义
     */
    private String meaning;
    /**
     * 单词音标
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
}
