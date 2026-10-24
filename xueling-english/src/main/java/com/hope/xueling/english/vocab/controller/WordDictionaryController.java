package com.hope.xueling.english.vocab.controller;

import com.hope.xueling.common.domain.vo.Result;
import com.hope.xueling.english.vocab.domain.vo.WordDictionaryVO;
import com.hope.xueling.english.vocab.service.IWordDictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 词典控制器：按单词查询词条释义、音标与例句
 * @author 谢光湘
 * @since 2026/10/24
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/dictionary")
public class WordDictionaryController {
    private final IWordDictionaryService wordDictionaryService;

    /**
     * 翻译单词
     * @param word 单词
     * @return WordDictionaryVO 翻译结果
     */
    @GetMapping
    public Result<WordDictionaryVO> translateWord(@RequestParam("word") String word) {
        WordDictionaryVO wordDictionary = wordDictionaryService.queryWordDictionary(word);
        log.info("查询结果: {}", wordDictionary);
        return Result.success(wordDictionary);
    }
}
