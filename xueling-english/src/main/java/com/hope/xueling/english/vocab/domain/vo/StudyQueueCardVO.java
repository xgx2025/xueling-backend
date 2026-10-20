package com.hope.xueling.english.vocab.domain.vo;

import lombok.Data;

import java.time.Instant;

@Data
public class StudyQueueCardVO {
    private String wordId;
    private String word;
    private String phonetic;
    private String meaning;
    private String example;
    private String exampleTranslation;
    private String imageUrl;
    private String state;
    private Instant dueAt;
    private int version;
    private boolean newCard;
}
