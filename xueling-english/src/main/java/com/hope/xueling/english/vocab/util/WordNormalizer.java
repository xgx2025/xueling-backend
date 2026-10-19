package com.hope.xueling.english.vocab.util;

import com.hope.xueling.common.exception.ValidationException;
import java.text.Normalizer;
import java.util.Locale;

/** 词典导入和查询必须使用同一套规范化规则。 */
public final class WordNormalizer {
    private WordNormalizer() {}

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
