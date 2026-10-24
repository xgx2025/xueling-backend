package com.hope.xueling.common.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

/**
 * 智能阅读助手
 * @author 谢光益
 * @since 2026/1/30
 */
@AiService(wiringMode = EXPLICIT,chatModel = "DeepseekReasonerModel")
public interface SmartReadingAssistant {

    /**
     * 英语句子分析
     * @param message 英语句子
     * @return 解析结果
     */
    @SystemMessage("/prompt/analyzeSentence.txt")
    String analyzeEnglishSentence(String message);

    /**
     * 词汇短语汇总
     * @param message 词汇短语
     * @return 汇总结果
     */
    @SystemMessage("/prompt/vocabularyPhrases.txt")
    String summarizeEnglishPhrases(String message);

    /**
     * 生成阅读测试题（根据文章内容生成指定难度的阅读测试题2个）
     * @param message 阅读文章和测试难度级别（简单/中等/困难）
     * @return 测试题
     */
    @SystemMessage("/prompt/readTest.txt")
    String generateReadingTest(String message);

    /**
     * 文章感悟（根据文章内容生成文章感悟）
     * @param message 阅读文章
     * @return 文章感悟
     */
    @SystemMessage("/prompt/readInsights.txt")
    String generateArticleThoughts(String message);

    /**
     * 英语文章格式化
     * @param message 英语文章
     * @return 格式化后的英语文章
     */
    @SystemMessage("/prompt/formatEnglishArticle.txt")
    String formatEnglishArticle(String message);

    /**
     * 翻译英文标题
     * @param message 英文标题
     * @return 中文翻译
     */
    @SystemMessage("你是专业的英语老师，将英文标题翻译成中文，直接给出中文翻译，不要响应其它文字。")
    String translateTitle(String message);

    /**
     * 生成图片提示词
     * @param message 阅读文章
     * @return 图片提示词
     */
    @SystemMessage("/prompt/generateImagePrompt.txt")
    String generateImagePrompt(String message);

    /**
     * 将文章内容翻译成中文
     */
    @SystemMessage("/prompt/translateArticle.txt")
    String translateArticle(String message);
}
