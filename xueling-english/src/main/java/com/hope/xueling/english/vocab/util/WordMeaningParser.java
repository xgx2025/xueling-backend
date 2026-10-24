package com.hope.xueling.english.vocab.util;

import com.hope.xueling.english.vocab.domain.vo.WordTranslationVO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 释义解析工具：将 ECDICT 的原始多行释义转换为可展示的义项，无法识别词性的内容保留为“其他”。
 * @author 谢光湘
 * @since 2026/10/24
 */
public final class WordMeaningParser {
    private static final Pattern POS = Pattern.compile(
            "(?i)(?<![a-z])(n|v|vt|vi|adj|adv|prep|conj|pron|int|interj|num|art|aux|pl|abbr|a|ad)\\.\\s*");

    private WordMeaningParser() {}

    /**
     * 把 ECDICT 的多行释义按词性前缀切分为义项；无法识别词性的内容归入“其他”
     * @param meaning 原始释义文本，一行内可能包含多个词性片段
     * @return 按词性分组的义项列表，无内容时返回空列表
     */
    public static List<WordTranslationVO> parse(String meaning) {
        if (meaning == null || meaning.isBlank()) {
            return List.of();
        }
        List<WordTranslationVO> result = new ArrayList<>();
        for (String line : meaning.replace("\\n", "\n").split("\\R")) {
            if (line.isBlank()) continue;
            Matcher matcher = POS.matcher(line);
            List<Integer> starts = new ArrayList<>();
            List<Integer> ends = new ArrayList<>();
            List<String> labels = new ArrayList<>();
            while (matcher.find()) {
                starts.add(matcher.start());
                ends.add(matcher.end());
                labels.add(matcher.group(1).toLowerCase() + ".");
            }
            if (starts.isEmpty()) {
                /**
                 * 把一段释义按分号切分后追加为一条词性义项，空白内容忽略
                 * @param result 结果列表
                 * @param pos 词性标签
                 * @param text 该词性下的释义文本
                 */
                add(result, "其他", line);
                continue;
            }
            if (starts.get(0) > 0) add(result, "其他", line.substring(0, starts.get(0)));
            for (int i = 0; i < starts.size(); i++) {
                int end = i + 1 < starts.size() ? starts.get(i + 1) : line.length();
                add(result, labels.get(i), line.substring(ends.get(i), end));
            }
        }
        return result;
    }

    private static void add(List<WordTranslationVO> result, String pos, String text) {
        List<String> meanings = Arrays.stream(text.replaceFirst("^[；;,\\s]+", "")
                        .replaceFirst("[；;,\\s]+$", "").split("[；;]"))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();
        if (!meanings.isEmpty()) result.add(new WordTranslationVO(pos, meanings));
    }
}
