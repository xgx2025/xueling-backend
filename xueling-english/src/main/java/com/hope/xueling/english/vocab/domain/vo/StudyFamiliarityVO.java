package com.hope.xueling.english.vocab.domain.vo;

import java.util.List;

public record StudyFamiliarityVO(int learnedCount, int learningCount, int consolidatingCount,
                                 int stableCount, int dueCount, int wordCount, int page,
                                 int pageSize, List<StudyFamiliarityWordVO> words,
                                 List<StudyFamiliarityWordVO> attentionWords) {}
