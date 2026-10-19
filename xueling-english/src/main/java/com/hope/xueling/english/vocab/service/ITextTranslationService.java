package com.hope.xueling.english.vocab.service;

/**
 * 返回纯文本的翻译服务接口
 * @author 谢光益
 * @since 2026/1/26
 */
public interface ITextTranslationService{
    /**
     * 翻译单词
     * @param word 单词
     * @return 翻译文本
     */
    String translateWord(String word);
}
