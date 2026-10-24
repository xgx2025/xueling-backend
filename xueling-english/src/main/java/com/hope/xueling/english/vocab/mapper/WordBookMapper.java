package com.hope.xueling.english.vocab.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hope.xueling.english.vocab.domain.entity.WordBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * 词书 Mapper，仅使用 MyBatis-Plus 通用 CRUD
 * @author 谢光湘
 * @since 2026/10/24
 */
@Mapper
public interface WordBookMapper extends BaseMapper<WordBook> {
}
