package com.hope.xueling.english.vocab.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hope.xueling.common.exception.ValidationException;
import com.hope.xueling.common.util.StrUtils;
import com.hope.xueling.english.vocab.domain.dto.WordDictionaryDTO;
import com.hope.xueling.english.vocab.domain.entity.WordBookDictionaryRelation;
import com.hope.xueling.english.vocab.domain.entity.WordDictionary;
import com.hope.xueling.english.vocab.domain.vo.WordDictionaryVO;
import com.hope.xueling.english.vocab.domain.vo.WordVO;
import com.hope.xueling.english.vocab.mapper.WordBookDictionaryRelationMapper;
import com.hope.xueling.english.vocab.mapper.WordDictionaryMapper;
import com.hope.xueling.english.vocab.service.IWordDefinitionService;
import com.hope.xueling.english.vocab.service.IWordDictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 单词字典服务实现类
 * @author 谢光湘
 * @since 2026/1/25
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WordDictionaryServiceImpl implements IWordDictionaryService {
    private final IWordDefinitionService wordDefinitionService;
    private final WordDictionaryMapper wordDictionaryMapper;
    private final WordBookDictionaryRelationMapper wordBookDictionaryRelationMapper;


    @Override
    public WordDictionaryVO queryWordDictionary(String word) {
        String keyword = word.trim();
        log.info("查询单词: {}", keyword);
        if (keyword.length()>20){
            throw new ValidationException("单词长度太长");
        }
        String english = StrUtils.extractEnglish(keyword);
        String chinese = StrUtils.extractChinese(keyword);
        QueryWrapper<WordDictionary> queryWrapper = new QueryWrapper<>();
        //如果有英文则以英文为准
        if (!english.isEmpty()) {
            keyword = english;
            //TODO：目前词库不足，只能完全匹配，后续可以改为模糊匹配
            queryWrapper.eq("word", keyword);
        } else if (!chinese.isEmpty()) {
            keyword = chinese;
            queryWrapper.like("meaning", keyword);
        }

        WordDictionary entity = wordDictionaryMapper.selectOne(queryWrapper);
        WordDictionaryVO vo = new WordDictionaryVO();
        if (entity != null) {
            BeanUtils.copyProperties(entity, vo);
            QueryWrapper<WordBookDictionaryRelation> wbQueryWrapper = new QueryWrapper<>();
            wbQueryWrapper.eq("word_id", entity.getId());
            WordBookDictionaryRelation relation = wordBookDictionaryRelationMapper.selectOne(wbQueryWrapper);
            if (relation != null) {
                vo.setInWordBook(true);
                vo.setWordBookId(relation.getWordBookId().toString());
            }
            return vo;
        }
        // 从翻译服务查询翻译
        WordDictionaryDTO translation = wordDefinitionService.translateWord(keyword);
        if (translation == null || translation.getWord().trim().isEmpty() || translation.getMeaning().trim().isEmpty()) {
            log.warn("翻译服务未返回翻译结果，单词：{}", keyword);
            return null;
        }
        // 保存到数据库
        Long id = IdUtil.getSnowflakeNextId();
        saveWordDictionary(id,translation);
        vo.setId(id.toString());
        BeanUtils.copyProperties(translation, vo);
        return vo;
    }

    @Override
    public List<WordVO> getWordDetailByIds(List<String> wordIds) {
        List<WordDictionary> wordDictionaryList = wordDictionaryMapper.selectByIds(wordIds);
       return wordDictionaryList.stream().map(wordDictionary -> {
            WordVO vo = new WordVO();
            BeanUtils.copyProperties(wordDictionary, vo);
            return vo;
        }).toList();
    }

    @Override
    public void saveWordDictionary(Long id,WordDictionaryDTO wordDictionary) {
        // 转换为实体类
        WordDictionary entity = new WordDictionary();
        entity.setId(id);
        BeanUtils.copyProperties(wordDictionary, entity);
        wordDictionaryMapper.insert(entity);
    }
}
