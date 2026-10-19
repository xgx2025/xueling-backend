package com.hope.xueling.english.vocab.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * 单词家族节点视图对象
 * @author 谢光湘
 * @since 2026/1/28
 */
@Data
@NoArgsConstructor
public class WordFamilyNodeVO {
    /**
     * 节点文本（如"beautiful"）
     */
    private String name;
    /**
     * 节点类型（如"root"、"pos"、"word"、"meaning"）
     */
    private String type;
    /**
     * 仅词性节点：adj/adv/n/v（前端映射样式用）
     */
    private String posCode;
    /**
    * 子节点列表
    */
    private List<WordFamilyNodeVO> children = new ArrayList<>();


    /**
     * 创建根节点
     * @param baseWord 基础单词（如"beautiful"）
     * @return 根节点视图对象
     */
    public static WordFamilyNodeVO createRoot(String baseWord) {
        WordFamilyNodeVO root = new WordFamilyNodeVO();
        root.setName(baseWord+"家族");
        root.setType("root");
        return root;
    }

    /**
     * 创建词性节点
     * @param posCode 词性代码（如"adj"）
     * @param chinesePos 词性中文（如"形容词"）
     * @return 词性节点视图对象
     */
    public static WordFamilyNodeVO createPosNode(String posCode, String chinesePos) {
        WordFamilyNodeVO node = new WordFamilyNodeVO();
        node.setName(posCode + ". " + chinesePos);
        node.setType("pos");
        node.setPosCode(posCode);
        return node;
    }

    /**
     * 创建单词节点
     * @param word 单词（如"beautiful"）
     * @return 单词节点视图对象
     */
    public static WordFamilyNodeVO createWordNode(String word) {
        WordFamilyNodeVO node = new WordFamilyNodeVO();
        node.setName(word);
        node.setType("word");
        return node;
    }

    /**
     * 创建释义节点
     * @param meaning 释义（如"美丽的"）
     * @return 释义节点视图对象
     */
    public static WordFamilyNodeVO createMeaningNode(String meaning) {
        WordFamilyNodeVO node = new WordFamilyNodeVO();
        node.setName(meaning);
        node.setType("meaning");
        return node;
    }
}
