package com.hope.xueling.english.vocab.domain.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单词词性DTO(用于存储单词和词性)
 * @author 谢光湘
 * @since 2026/1/28
 */
@Data
@NoArgsConstructor
public class WordPosDTO {
    /**
     * 词性代码（如"adj"）
     */
    private String posCode;
    /**
     * 单词（如"beautiful"）
     */
    private String word;
}
