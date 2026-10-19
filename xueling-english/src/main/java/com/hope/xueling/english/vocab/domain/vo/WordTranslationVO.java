package com.hope.xueling.english.vocab.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class WordTranslationVO {
    private String pos;
    private List<String> meanings;
}
