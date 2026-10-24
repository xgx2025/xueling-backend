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

    /**
     * 所属分类 ID
     */
    private Long categoryId;
    /**
     * 标签文本
     */
    private String tag;
    /**
     * 中文翻译
     */
    private String chineseMeaning;
    /**
     * 重点词汇与短语
     */
    private String vocabularyPhrasesSummary;
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
    /**
     * 最后更新时间
     */
    private String updateTime;
    /**
     * 预留的高亮标注数据，当前未填充，默认为空列表
     */
    private Map<String, Object> highlights = Map.of("highlights", List.of());
}
