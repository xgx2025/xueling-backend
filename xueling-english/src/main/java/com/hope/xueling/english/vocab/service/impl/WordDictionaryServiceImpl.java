package com.hope.xueling.english.vocab.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hope.xueling.common.exception.BusinessException;
import com.hope.xueling.common.util.ThreadLocalUtils;
import com.hope.xueling.english.vocab.domain.dto.WordDictionaryDTO;
import com.hope.xueling.english.vocab.domain.entity.WordBook;
import com.hope.xueling.english.vocab.domain.entity.WordBookDictionaryRelation;
import com.hope.xueling.english.vocab.domain.entity.WordDictionary;
import com.hope.xueling.english.vocab.domain.vo.WordDictionaryVO;
import com.hope.xueling.english.vocab.domain.vo.WordVO;
import com.hope.xueling.english.vocab.mapper.WordBookDictionaryRelationMapper;
import com.hope.xueling.english.vocab.mapper.WordBookMapper;
import com.hope.xueling.english.vocab.mapper.WordDictionaryMapper;
import com.hope.xueling.english.vocab.service.IWordDefinitionService;
import com.hope.xueling.english.vocab.service.IWordDictionaryService;
import com.hope.xueling.english.vocab.util.WordMeaningParser;
import com.hope.xueling.english.vocab.util.WordNormalizer;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class WordDictionaryServiceImpl implements IWordDictionaryService {
    private final IWordDefinitionService wordDefinitionService;
    private final WordDictionaryMapper wordDictionaryMapper;
    private final WordBookDictionaryRelationMapper wordBookDictionaryRelationMapper;
    private final WordBookMapper wordBookMapper;

    @Override
    public WordDictionaryVO queryWordDictionary(String word) {
        String key = WordNormalizer.normalize(word);
        WordDictionary entity = findByKey(key);
        if (entity != null && entity.getMeaning() != null && !entity.getMeaning().isBlank()) {
            return toVO(entity);
        }

        WordDictionaryDTO translation;
        try {
            translation = wordDefinitionService.translateWord(word.trim());
        } catch (RuntimeException ex) {
            log.error("AI 翻译失败: {}", key, ex);
            throw new BusinessException("翻译服务暂不可用，请稍后重试");
        }
        if (translation == null || translation.getMeaning() == null || translation.getMeaning().isBlank()) {
            throw new BusinessException("没有找到可靠的释义，请检查拼写");
        }

        if (entity == null) {
            entity = new WordDictionary();
            entity.setWord(translation.getWord().trim());
            entity.setWordKey(key);
            entity.setSource("AI");
            entity.setMeaning(translation.getMeaning());
            entity.setPhonetic(translation.getPhonetic());
            entity.setExample(translation.getExample());
            entity.setExampleTranslation(translation.getExampleTranslation());
            try {
                wordDictionaryMapper.insert(entity);
            } catch (DuplicateKeyException ex) {
                entity = findByKey(key);
                if (entity == null) throw ex;
                if (entity.getMeaning() == null || entity.getMeaning().isBlank()) {
                    enrich(entity, translation);
                }
            }
        } else {
            enrich(entity, translation);
        }
        return toVO(entity);
    }

    @Override
    public List<WordVO> getWordDetailByIds(List<String> wordIds) {
        return wordDictionaryMapper.selectByIds(wordIds).stream().map(entity -> {
            WordVO vo = new WordVO();
            BeanUtils.copyProperties(entity, vo);
            vo.setId(String.valueOf(entity.getId()));
            return vo;
        }).toList();
    }

    private WordDictionary findByKey(String key) {
        return wordDictionaryMapper.selectOne(new QueryWrapper<WordDictionary>().eq("word_key", key));
    }

    private void enrich(WordDictionary entity, WordDictionaryDTO translation) {
        entity.setMeaning(translation.getMeaning());
        entity.setSource("AI_ENRICHED");
        if (entity.getPhonetic() == null || entity.getPhonetic().isBlank()) {
            entity.setPhonetic(translation.getPhonetic());
        }
        entity.setExample(translation.getExample());
        entity.setExampleTranslation(translation.getExampleTranslation());
        wordDictionaryMapper.updateById(entity);
    }

    private WordDictionaryVO toVO(WordDictionary entity) {
        WordDictionaryVO vo = new WordDictionaryVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setId(String.valueOf(entity.getId()));
        vo.setTranslations(WordMeaningParser.parse(entity.getMeaning()));
        vo.setInWordBook(false);
        Claims claims = ThreadLocalUtils.get();
        if (claims != null) {
            Long userId = claims.get("userId", Long.class);
            List<Long> bookIds = wordBookMapper.selectList(new QueryWrapper<WordBook>()
                            .select("id").eq("user_id", userId).eq("is_deleted", 0))
                    .stream().map(WordBook::getId).toList();
            if (!bookIds.isEmpty()) {
                List<WordBookDictionaryRelation> relations = wordBookDictionaryRelationMapper.selectList(
                        new QueryWrapper<WordBookDictionaryRelation>().eq("word_id", entity.getId())
                                .in("word_book_id", bookIds).last("LIMIT 1"));
                if (!relations.isEmpty()) {
                    vo.setInWordBook(true);
                    vo.setWordBookId(String.valueOf(relations.get(0).getWordBookId()));
                }
            }
        }
        return vo;
    }
}
