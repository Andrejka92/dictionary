package com.andrejka.dictionary;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document
public class Words {

    @Id
    private String id;

    private String wordEN;
    private String wordLT;
    private String wordRU;
    private String sentanceWithThisWord;
}
