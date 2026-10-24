package com.hope.xueling.english.vocab.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 词书与词条的关联实体，对应 word_book_dictionary_relation 表，同一词书内同一词条唯一
 * @author 谢光湘
 * @since 2026/10/24
 */
@Data
@NoArgsConstructor
@TableName("word_book_dictionary_relation")
public class WordBookDictionaryRelation {
    /**
     * 主键ID（由 MyBatis-Plus 雪花算法统一生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 单词书ID
     */
    private Long wordBookId;

    /**
     * 单词字典ID
     */
    private Long wordId;
    /**
     * 词书内排序值
     */
    private Integer sortOrder;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
