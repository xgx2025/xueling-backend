package com.hope.xueling.english.vocab.domain.vo;

/**
 * 按复习次数分档的词数分布
 * @param once 仅复习过 1 次的词数
 * @param twice 复习过 2 次的词数
 * @param threeToFive 复习过 3 至 5 次的词数
 * @param sixOrMore 复习过 6 次及以上的词数
 * @author 谢光湘
 * @since 2026/10/24
 */
public record StudyReviewCountVO(int once, int twice, int threeToFive, int sixOrMore) {}
