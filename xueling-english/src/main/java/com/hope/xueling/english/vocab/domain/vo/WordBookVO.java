package com.hope.xueling.english.vocab.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WordBookVO {
    /**
     * 单词本ID
     */
    private String id;
    /**
     * 单词本名称
     */
    private String name;
    /**
    * 单词本颜色
    */
    private String color;
    /**
     * 单词本图标
     */
    private String icon;
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

}
