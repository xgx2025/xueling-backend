package com.hope.xueling.english.vocab.service.impl;

import com.hope.xueling.common.ai.SmartTranslation;
import com.hope.xueling.common.exception.ValidationException;
import com.hope.xueling.english.vocab.service.ITextTranslationService;
import org.springframework.stereotype.Service;

/**
 * 翻译服务实现类
 * @author 谢光益
 * @since 2026/1/26
 */
@Service
public class TextTranslationServiceImpl implements ITextTranslationService {

    //智能翻译服务
    private final SmartTranslation smartTranslation;

    public TextTranslationServiceImpl(SmartTranslation smartTranslation) {
        this.smartTranslation = smartTranslation;
    }

    @Override
    public String translateWord(String word) {
        // 检查单词是否为空
        if (word == null || word.isEmpty()) {
            throw new ValidationException("单词不能为空");
        }

        //正则表达式简单检查单词格式是否正确
        if (!word.matches("[a-zA-Z]+")) {
            throw new ValidationException("单词只能包含字母");
        }

        return smartTranslation.translateWord(word);
    }
}
