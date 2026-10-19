package com.hope.xueling.english.vocab.controller;

import com.hope.xueling.common.domain.vo.Result;
import com.hope.xueling.common.util.ThreadLocalUtils;
import com.hope.xueling.english.vocab.domain.dto.AddWordsToWordBookDTO;
import com.hope.xueling.english.vocab.domain.dto.CreateWordBookDTO;
import com.hope.xueling.english.vocab.domain.dto.RemoveWordsFromWordBookDTO;
import com.hope.xueling.english.vocab.domain.vo.WordBookDetailVO;
import com.hope.xueling.english.vocab.domain.vo.WordBookVO;
import com.hope.xueling.english.vocab.domain.vo.WordDictionaryVO;
import com.hope.xueling.english.vocab.service.IWordBookService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 单词本控制器
 * @author 谢光湘
 * @since 2026/1/26
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/wordbooks")
public class WordBookController {
    private final IWordBookService wordBookService;

    /**
     * 创建一个单词本
     * @param createWordBookDTO 单词本DTO
     */
    @PostMapping
    public Result<String> createWordBook(@RequestBody CreateWordBookDTO createWordBookDTO) {
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        wordBookService.createWordBook(createWordBookDTO,userId);
        return Result.success("单词本创建成功");
    }

    /**
     * 获取用户所有单词本
     * @return 用户所有单词本列表
     */
    @GetMapping("/all")
    public Result<List<WordBookVO>> getWordBooks() {
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        List<WordBookVO> wordBookVoList = wordBookService.getWordBooks(userId);
        return Result.success(wordBookVoList);
    }

    /**
     * 获取单词本详情
     * @param wordBookId 单词本ID
     * @return 单词本详情
     */
    @GetMapping("/{wordBookId}")
    public Result<WordBookDetailVO> getWordBookDetail(@PathVariable("wordBookId") Long wordBookId) {
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        WordBookDetailVO wordBookVo = wordBookService.getWordBookDetail(wordBookId, userId);
        return Result.success(wordBookVo);
    }

    /**
     * 匹配用户输入的单词是否参在于系统的单词库中
     * @param words 用户输入的单词列表
     * @return 匹配到的单词列表
     */
    @PostMapping("/match")
    public Result<List<WordDictionaryVO>> matchWords(@RequestParam("words") String words) {
        List<WordDictionaryVO> matchedWords = wordBookService.matchWords(words);
        return Result.success(matchedWords);
    }

    /**
     * 将单词添加到单词本
     * @param addWordsToWordBookDTO 添加单词到单词本DTO
     */
    @PostMapping("/word")
    public Result<String> addWordToWordBook(@RequestBody AddWordsToWordBookDTO addWordsToWordBookDTO) {
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        wordBookService.addWordsToWordBook(addWordsToWordBookDTO,userId);
        return Result.success("单词添加成功");
    }
    /**
     * 从单词本中删除单词
     * @param removeWordsFromWordBookDTO 从单词本中删除单词DTO
     */
    @DeleteMapping("/word")
    public Result<String> deleteWordFromWordBook(@RequestBody RemoveWordsFromWordBookDTO removeWordsFromWordBookDTO) {
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        wordBookService.deleteWordsFromWordBook(removeWordsFromWordBookDTO, userId);
        return Result.success("单词删除成功");
    }



}
