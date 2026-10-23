package com.hope.xueling.english.reading.domain.vo;

import lombok.Data;

@Data
public class ArticleListVO {
    private Long id;
    private String title;
    private String chineseTitle;
    private Long categoryId;
    private String tag;
    private String author;
    private String imageUrl;
    private Boolean isFree;
    private String createTime;
}
