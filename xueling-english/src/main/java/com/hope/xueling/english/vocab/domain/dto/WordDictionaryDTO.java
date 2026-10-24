package com.hope.xueling.english.vocab.domain.dto;

import lombok.Data;
import java.util.List;
/**
 * 单词字典数据传输对象（用于AI翻译的响应）
 * @author 谢光湘
 * @since 2026/1/26
 */
@Data
public class WordDictionaryDTO {
    /**
     * 单词
     */
    private String word;
    /**
     * 单词含义
     */
    private String meaning;
    /**
     * 按词性分组的释义列表
     */
    private List<WordTranslationDTO> translations;
    /**
     * 单词音标
     */
    private String phonetic;
    /**
     * 英文例句
     */
    private String example;
     /**
     * 例句中文翻译
     */
    private String exampleTranslation;
}
