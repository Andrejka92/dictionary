package com.andrejka.dictionary.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    public Words getWordById(String id) {
        return mongoTemplate.findById(id, Words.class);
    }

    public List<Words> getWordsByIds(List<String> ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(ids)); // Use "_id" if you're referring to the default MongoDB ID

        List<Words> words = mongoTemplate.find(query, Words.class);

        return words;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> getRandomWords(Integer count, String language) {
        // Define the projection based on the selected language
        String selectedField;
        if ("LT".equalsIgnoreCase(language)) {
            selectedField = "wordLT";
        } else if ("RU".equalsIgnoreCase(language)) {
            selectedField = "wordRU";
        } else {
            throw new IllegalArgumentException("Invalid language specified. Choose 'LT' or 'RU'.");
        }

        // Create an aggregation with $sample to randomly select 'count' documents
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.sample(count),
                Aggregation.project(selectedField, "wordENG") // Only include the selected language field and wordENG
        );

        // Execute the aggregation query and return List<Object>
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "words", Map.class);

        // Convert the result to List<Map<String, String>> with all values as String
        return results.getMappedResults().stream().map(document -> {
            // Create a new map with all entries converted to String
            Map<String, String> stringMap = new HashMap<>();
            document.forEach((key, value) -> {
                stringMap.put((String) key, value != null ? value.toString() : "");
            });
            return stringMap;
        }).collect(Collectors.toList());
    }

    public void updateWord(Words word) {
        mongoTemplate.save(word);
    }

    public Words findWordById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, Words.class);
    }
}
