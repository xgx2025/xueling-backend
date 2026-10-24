package com.hope.xueling.english.vocab.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 单词字典实体类
 * @author 谢光湘
 * @since 2026/1/25
 */
@Data
@NoArgsConstructor
public class WordDictionary {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 单词
     */
    private String word;
    /**
     * 规范化检索键（小写、去多余空格），唯一，用于跨词书去重
     */
    private String wordKey;
    /**
     * 释义
     */
    private String meaning;
    /**
     * 音标
     */
    private String phonetic;
    /**
     * 英文释义
     */
    private String definition;
    /**
     * 词性分布，形如 n:64/v:36，表示各词性占比
     */
    private String pos;
    /**
     * 柯林斯星级，0 表示未收录
     */
    private Integer collins;
    /**
     * 是否牛津 3000 核心词：1 是、0 否
     */
    private Integer oxford;
    /**
     * 考试大纲标签，空格分隔，如 cet4 cet6 ky
     */
    private String tag;
    /**
     * BNC 语料库词频排名
     */
    private Integer bnc;
    /**
     * 当代语料库词频排名
     */
    private Integer frq;
    /**
     * 词形变化，形如 p:did/d:done/i:doing/3:does
     */
    private String exchange;
    /**
     * 扩展详情（JSON 文本）
     */
    private String detail;
    /**
     * 发音音频地址
     */
    private String audio;
    /**
     * 词条来源，如 ECDICT、ADMIN_IMPORT
     */
    private String source;
    /**
     * 导入时所依据的数据集版本号
     */
    private String datasetVersion;
    /**
     * 英文例句
     */
    private String example;
    /**
     * 例句中文翻译
     */
    private String exampleTranslation;
     /**
      * 单词插图URL
      */
    private String imageUrl;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
