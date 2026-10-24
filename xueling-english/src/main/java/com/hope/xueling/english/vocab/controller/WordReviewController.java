package com.hope.xueling.english.vocab.controller;

import com.hope.xueling.common.domain.vo.Result;
import com.hope.xueling.english.vocab.domain.dto.ReviewWordListDTO;
import com.hope.xueling.english.vocab.domain.vo.WordFamilyNodeVO;
import com.hope.xueling.english.vocab.domain.vo.WordVO;
import com.hope.xueling.english.vocab.service.IWordDictionaryService;
import com.hope.xueling.english.vocab.service.IWordReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 单词复习辅助控制器：按 ID 批量取词条详情、查询单词族（同根词的不同词性）
 * @author 谢光湘
 * @since 2026/10/24
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/word-review")
public class WordReviewController {
    private final IWordReviewService wordReviewService;
    private final IWordDictionaryService wordDictionaryService;


    /**
     * 通过wordIds获取单词详情
     * @param wordIds 单词ID列表
     * @return 单词详情列表
     */
    @PostMapping("/word-detail")
    public Result<List<WordVO>> getWordDetail(@RequestBody ReviewWordListDTO wordIds) {
        List<WordVO> wordDictionaryVOS = wordDictionaryService.getWordDetailByIds(wordIds.getWordIds());
        return Result.success(wordDictionaryVOS);
    }

    /**
     * 获取单词的单词族（各种词性）
     * @param word 单词
     * @return 单词族
     */
    @GetMapping("/family")
    public Result<WordFamilyNodeVO> getWordFamily(String word) {
        WordFamilyNodeVO wordFamilyNodeVO = wordReviewService.getWordFamilyTree(word);
        return Result.success(wordFamilyNodeVO);
    }




}
