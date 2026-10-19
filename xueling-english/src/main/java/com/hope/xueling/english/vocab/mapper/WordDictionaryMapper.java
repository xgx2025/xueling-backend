package com.hope.xueling.english.vocab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hope.xueling.english.vocab.domain.entity.WordDictionary;
import org.apache.ibatis.annotations.Mapper;

/**
 * 单词字典Mapper接口
 * @author 谢光湘
 * @since 2026/1/25
 */
@Mapper
public interface WordDictionaryMapper extends BaseMapper<WordDictionary> {
}
