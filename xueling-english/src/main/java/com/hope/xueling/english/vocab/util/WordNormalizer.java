package com.hope.xueling.english.vocab.util;

import com.hope.xueling.common.exception.ValidationException;
import java.text.Normalizer;
import java.util.Locale;

/**
 * 单词规范化工具：词典导入与查询必须使用同一套规则，否则同一单词会生成不同的 word_key。
 * <p>规则为 NFKC 归一化、去首尾空白、连续空白折叠为单个空格、转小写。</p>
 * @author 谢光湘
 * @since 2026/10/24
 */
public final class WordNormalizer {
    private WordNormalizer() {}

    /**
     * 将单词或词组规范化为检索键
     * @param word 原始单词或词组
     * @return 规范化后的检索键
     */
    public static String normalize(String word) {
        if (word == null) {
            throw new ValidationException("请输入英文单词或词组");
        }
        String key = Normalizer.normalize(word, Normalizer.Form.NFKC)
                .trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
        if (key.isEmpty() || key.length() > 255 || !key.matches(".*\\p{IsLatin}.*")) {
            throw new ValidationException("请输入不超过255字的英文单词或词组");
        }
        return key;
    }
}
