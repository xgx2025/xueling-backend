package com.hope.xueling.english.reading.domain.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 文章明细实体类
 * @author 谢光益
 * @since 2026/1/26
 */
@Data
public class Article {
    /**
     * 文章ID
     */
    private Long id;

    /**
     * 作者
     */
    private String author;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 中文标题
     */
     private String chineseTitle;

    /**
     * 文章内容
     * 格式：每个小标题前加##，段落之间用\n分隔，句子之间用&分隔
     */
    private String content;

    /**
     * 文章感悟
     */
    private String articleInsights;

    private Long categoryId;
    private String tag;
    private String chineseMeaning;
    private String vocabularyPhrasesSummary;
    private String imageUrl;
    private Boolean isFree;
    private String createTime;
    private String updateTime;
    private Map<String, Object> highlights = Map.of("highlights", List.of());
}
