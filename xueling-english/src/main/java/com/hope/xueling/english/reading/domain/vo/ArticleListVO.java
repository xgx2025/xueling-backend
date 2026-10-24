package com.hope.xueling.english.reading.domain.vo;

import lombok.Data;

/**
 * 文章摘要视图对象，用于阅读列表展示，不包含正文
 * @author 谢光湘
 * @since 2026/10/24
 */
@Data
public class ArticleListVO {
    /**
     * 文章 ID
     */
    private Long id;
    /**
     * 英文标题
     */
    private String title;
    /**
     * 中文标题
     */
    private String chineseTitle;
    /**
     * 所属分类 ID
     */
    private Long categoryId;
    /**
     * 标签文本
     */
    private String tag;
    /**
     * 原文作者
     */
    private String author;
    /**
     * 封面图地址
     */
    private String imageUrl;
    /**
     * 是否免费文章
     */
    private Boolean isFree;
    /**
     * 创建时间
     */
    private String createTime;
}
