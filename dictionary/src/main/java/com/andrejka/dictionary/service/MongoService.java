package com.andrejka.dictionary.service;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.andrejka.dictionary.Words;

@Service
public class MongoService {

    private final MongoTemplate mongoTemplate;

    public MongoService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void saveWord(Words word) {
        mongoTemplate.insert(word);
    }

    public List<Words> getAllWords() {
        return mongoTemplate.findAll(Words.class);
    }

    public void deleteWord(String id) {
        mongoTemplate.remove(mongoTemplate.findById(id, Words.class));
    }
}
