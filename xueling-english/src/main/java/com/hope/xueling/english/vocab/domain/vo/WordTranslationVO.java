package com.hope.xueling.english.vocab.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

/**
 * 按词性分组的释义视图
 * @author 谢光湘
 * @since 2026/10/24
 */
@Data
@AllArgsConstructor
public class WordTranslationVO {
    /**
     * 词性，如 n.、v.、adj.
     */
    private String pos;
    /**
     * 该词性下的释义列表
     */
    private List<String> meanings;
}
