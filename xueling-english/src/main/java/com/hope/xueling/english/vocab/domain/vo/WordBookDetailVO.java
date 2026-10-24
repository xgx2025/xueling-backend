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
    /** 已学习词数占比（0-100），不代表已掌握。 */
    private Integer learningCoverage;
    /** 兼容旧客户端；新客户端应使用 learningCoverage。 */
    private Integer mastery;
    /**
     * 已学习词数
     */
    private Integer learnedCount;
    /**
     * 该词书是否已纳入学习计划
     */
    private Boolean studyEnabled;
    /**
     * 词书类型：SYSTEM 系统词书、USER 用户自建词书
     */
    private String bookType;
    /**
     * 单词列表
     */
    private List<WordVO> wordList;
}
