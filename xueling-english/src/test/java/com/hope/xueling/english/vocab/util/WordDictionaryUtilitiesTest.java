package com.hope.xueling.english.vocab.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WordDictionaryUtilitiesTest {
    @Test
    void normalizerPreservesPhrasesAndPunctuation() {
        assertEquals("can't", WordNormalizer.normalize("  Can't  "));
        assertEquals("ice cream", WordNormalizer.normalize("ICE   CREAM"));
        assertEquals("long-time", WordNormalizer.normalize("Long-Time"));
    }

    @Test
    void parserKeepsUnclassifiedTranslation() {
        var translations = WordMeaningParser.parse("n. 苹果；果实\\n[网络] 苹果公司");
        assertEquals(2, translations.size());
        assertEquals("n.", translations.get(0).getPos());
        assertEquals(2, translations.get(0).getMeanings().size());
        assertEquals("其他", translations.get(1).getPos());
    }

    @Test
    void parserHandlesSeveralPartsOfSpeechOnOneLine() {
        var translations = WordMeaningParser.parse("n. 标志；旗帜；vt. 标记");
        assertEquals(2, translations.size());
        assertEquals("vt.", translations.get(1).getPos());
        assertEquals("标记", translations.get(1).getMeanings().get(0));
    }
}
