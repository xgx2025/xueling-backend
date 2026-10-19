package com.hope.xueling.english.vocab.service;

import com.hope.xueling.common.util.ThreadLocalUtils;
import com.hope.xueling.english.vocab.domain.dto.WordDictionaryDTO;
import com.hope.xueling.english.vocab.domain.entity.WordDictionary;
import com.hope.xueling.english.vocab.mapper.WordBookDictionaryRelationMapper;
import com.hope.xueling.english.vocab.mapper.WordBookMapper;
import com.hope.xueling.english.vocab.mapper.WordDictionaryMapper;
import com.hope.xueling.english.vocab.service.impl.WordDictionaryServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WordDictionaryServiceTest {
    private final IWordDefinitionService ai = mock(IWordDefinitionService.class);
    private final WordDictionaryMapper mapper = mock(WordDictionaryMapper.class);
    private final WordBookDictionaryRelationMapper relations = mock(WordBookDictionaryRelationMapper.class);
    private final WordBookMapper books = mock(WordBookMapper.class);
    private final WordDictionaryServiceImpl service = new WordDictionaryServiceImpl(ai, mapper, relations, books);

    @AfterEach
    void clearClaims() {
        ThreadLocalUtils.remove();
    }

    @Test
    void dictionaryHitDoesNotCallAi() {
        WordDictionary entry = new WordDictionary();
        entry.setId(11L);
        entry.setWord("ice cream");
        entry.setMeaning("冰淇淋");
        entry.setSource("ECDICT");
        when(mapper.selectOne(any())).thenReturn(entry);

        var result = service.queryWordDictionary(" ICE   CREAM ");
        assertEquals("11", result.getId());
        assertEquals("ECDICT", result.getSource());
        assertEquals("冰淇淋", result.getTranslations().get(0).getMeanings().get(0));
        verifyNoInteractions(ai);
    }

    @Test
    void missingWordCallsAiAndCachesIt() {
        when(mapper.selectOne(any())).thenReturn(null);
        WordDictionaryDTO translation = new WordDictionaryDTO();
        translation.setWord("novelword");
        translation.setMeaning("n. 新词");
        when(ai.translateWord("novelword")).thenReturn(translation);
        when(mapper.insert(any(WordDictionary.class))).thenAnswer(invocation -> {
            WordDictionary entry = invocation.getArgument(0);
            entry.setId(12L);
            return 1;
        });

        var result = service.queryWordDictionary("novelword");
        assertEquals("12", result.getId());
        assertEquals("AI", result.getSource());
        verify(mapper).insert(any(WordDictionary.class));
    }
}
