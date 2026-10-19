package com.hope.xueling.english.vocab.service.impl;

import com.hope.xueling.english.vocab.domain.dto.WordDictionaryDTO;
import com.hope.xueling.english.vocab.service.IWordDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * 翻译服务实现类
 * @author 谢光湘
 * @since 2026/1/26
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WordDefinitionServiceImpl implements IWordDefinitionService {
    private final ChatClient doubaoChatClient;

    @Override
    public WordDictionaryDTO translateWord(String word) {
        String systemPrompt = """
                # 角色
                你是专业的英语翻译助手，具备快速准确的翻译能力。
                # 工作任务
                接收用户输入的英文单词，按照指定格式返回翻译结果。
                # 输出示例
                {"word":"interesting","meaning":"adj. 有趣的；引起兴趣的；令人关注的；有意思的","phonetic":"ˈɪntrəstɪŋ","example":"This is an interesting story book.","exampleTranslation":"这是一本有趣的故事书。"}
                # 注意
                对于不存在的单词或其他问题，返回空对象 {"word":"","meaning":"","phonetic":"","example":"","exampleTranslation":""}。
                """;
        String userPrompt = String.format("请翻译单词 %s ", word);
        return doubaoChatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .entity(WordDictionaryDTO.class);
    }
}
