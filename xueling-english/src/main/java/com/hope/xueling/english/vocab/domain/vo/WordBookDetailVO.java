package com.hope.xueling.english.vocab.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 单词本详情VO
 * @author 谢光湘
 * @since 2026/1/26
 */
@Data
@NoArgsConstructor
public class WordBookDetailVO {
    /**
     * 单词本ID
     */
    private String id;
    /**
     * 单词书名称
     */
    private String name;
    /**
     * 单词总数
     */
    private Integer wordCount;
    /**
     * 掌握度（0-100）
     */
    private Integer mastery;
    private Integer learnedCount;
    private Boolean studyEnabled;
    private String bookType;
    /**
     * 单词列表
     */
    private List<WordVO> wordList;
}
