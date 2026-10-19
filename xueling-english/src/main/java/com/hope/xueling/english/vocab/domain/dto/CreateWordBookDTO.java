package com.hope.xueling.english.vocab.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateWordBookDTO {
    /**
     * 单词本名称
     */
    private String name;
     /**
     * 封面背景色
     */
    private String color;
    /**
     * 封面图标
     */
    private String icon;
}
