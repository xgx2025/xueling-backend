package com.hope.xueling.english.vocab.domain.dto;

import lombok.Data;
import java.util.List;

@Data
public class WordTranslationDTO {
    private String pos;
    private List<String> meanings;
}
