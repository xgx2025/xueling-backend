package com.hope.xueling.english.reading.domain.entity;

import lombok.Data;

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
}
