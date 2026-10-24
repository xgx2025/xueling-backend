package com.hope.xueling.english.vocab.service.impl;

import com.hope.xueling.english.vocab.domain.dto.WordDictionaryDTO;
import com.hope.xueling.english.vocab.domain.dto.WordPosDTO;
import com.hope.xueling.english.vocab.domain.dto.WordPosListDTO;
import com.hope.xueling.english.vocab.domain.vo.WordFamilyNodeVO;
import com.hope.xueling.english.vocab.service.IWordDefinitionService;
import com.hope.xueling.english.vocab.service.IWordReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 单词族服务实现：借助大模型识别同根不同词性的单词，再逐个取释义并组装成“根 → 词性 → 单词 → 释义”的树
 * @author 谢光湘
 * @since 2026/10/24
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WordReviewServiceImpl implements IWordReviewService {
    /**
     * DeepSeek 对话客户端，当前未参与单词族构建，保留备用
     */
    private final ChatClient deepseekChatClient;
    /**
     * 豆包对话客户端，用于识别其他词性的单词
     */
    private final ChatClient doubaoChatClient;
    /**
     * 词典释义服务，用于补齐每个单词的释义
     */
    private final IWordDefinitionService wordDefinitionService;

    /**
     * 词性缩写到中文名称的映射
     */
    public static final Map<String, String> POS_CHINESE_MAP = Map.of(
            "n", "名词",
            "v", "动词",
            "adj", "形容词",
            "adv", "副词",
            "pron", "代词",
            "prep", "介词",
            "conj", "连词",
            "interj", "感叹词"
    );


    /**
     * 构建单词族树：识别其他词性后，为每个词性创建词性节点、单词节点与释义节点
     * @param baseWord 基础单词
     * @return 单词族树根节点
     */
    @Override
    public WordFamilyNodeVO getWordFamilyTree(String baseWord) {
        WordPosListDTO wordPosList = new WordPosListDTO();
        WordPosListDTO otherPosResult = getWordOtherPos(baseWord);
        if (otherPosResult != null) {
            wordPosList = otherPosResult;
        }
        //设置根节点
        WordFamilyNodeVO root = WordFamilyNodeVO.createRoot(baseWord);
        for (WordPosDTO wordPosDTO : wordPosList.getWordPosList()) {
            //创建词性节点
            WordFamilyNodeVO posNode = WordFamilyNodeVO.createPosNode(wordPosDTO.getPosCode(), POS_CHINESE_MAP.get(wordPosDTO.getPosCode()));
            //创建单词节点
            WordFamilyNodeVO wordNode = WordFamilyNodeVO.createWordNode(wordPosDTO.getWord());
            WordDictionaryDTO wordDictionaryDTO = wordDefinitionService.translateWord(wordPosDTO.getWord());
            Map<String,String> posMap = parsePosToMapByStream(wordDictionaryDTO.getMeaning());
            //创建释义节点
            WordFamilyNodeVO meaningNode = WordFamilyNodeVO.createMeaningNode(posMap.get(wordPosDTO.getPosCode()));
            //将单词节点和释义节点添加到词性节点下
            wordNode.getChildren().add(meaningNode);
            posNode.getChildren().add(wordNode);
            //将词性节点添加到根节点下
            root.getChildren().add(posNode);
        }
        return root;
    }

    /**
     * 获取该单词的其他词性的单词（如名词、动词、形容词等）
     *
     * @param baseWord 基础单词（如"beautiful"）
     * @return 包含其他词性单词的映射，键为词性，值为对应的单词
     */
    public WordPosListDTO getWordOtherPos(String baseWord) {
        String systemPrompt = """
                # 角色
                你是专业的英语单词助手，具备识别单词词性的能力。
                # 工作任务
                接收用户输入的英文单词，返回该单词的其他词性的单词（如名词、动词、形容词等），用于用户对比学习。
                # 注意
                对于不存在的单词，返回空对象 {}。
                # 词性简称严格使用英文小写，如下：
                "n", "名词",
                "v", "动词",
                "adj", "形容词",
                "adv", "副词",
                "pron", "代词",
                "prep", "介词",
                "conj", "连词",
                "interj", "感叹词"
                """;
        String userPrompt = String.format("请返回单词 %s 的其他词性的单词(包含这个单词本身) ", baseWord);
        return doubaoChatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .entity(WordPosListDTO.class);
    }



    /**
     * Stream流式解析词性字符串为Map
     * @param posStr 格式：v. 喜欢；prep. 像；如同；conj. 好像
     * @return 解析后的Map
     */
    public Map<String, String> parsePosToMapByStream(String posStr) {
        // 边界判断：空字符串直接返回空Map
        if (posStr == null || posStr.isBlank()) {
            return Map.of(); // Java9+空Map，Java8用new HashMap<>()
        }

        // 流式解析核心逻辑
        return Arrays.stream(posStr.split("；")) // 按分号分割成流
                .map(String::trim) // 去首尾空格
                .filter(segment -> !segment.isBlank()) // 过滤空片段
                .map(segment -> segment.split("\\.", 2)) // 按第一个点分割成[词性, 释义]
                .filter(parts -> parts.length == 2) // 过滤格式错误的片段
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(), // Map的key：词性缩写（去空格）
                        parts -> parts[1].trim(), // Map的value：释义（去空格）
                        (oldVal, newVal) -> newVal // 解决重复词性的冲突：新值覆盖旧值
                ));
    }

}
