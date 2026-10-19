package com.hope.xueling.english.reading.domain.entity;

import lombok.Data;

/**
 * 阅读材料分类实体类
 * @author 谢光益
 * @since 2026/1/26
 */
@Data
public class ArticleCategory {
    /**
     * 阅读文章分类ID
     */
    private Long id;

     /**
      * 阅读文章分类名称
      */
    private String name;
}
