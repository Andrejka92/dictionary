package com.andrejka.dictionary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andrejka.dictionary.service.MongoService;

@RestController
@RequestMapping("/quiz")
public class DictionaryQuizRESTController {

    @Autowired
    private MongoService mongoService;

    @CrossOrigin(origins = "*")
    @GetMapping("/getQuizWords")
    public List<Map<String, String>> getQuizWords(@RequestParam("count") Integer count,
            @RequestParam("language") String language) {
        return mongoService.getRandomWords(count, language);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/submitResults")
    public Map<String, Object> submitQuizResults(@RequestParam("answers") List<String> userAnswers,
            @RequestParam("wordIds") List<String> wordIds, // Add word IDs as a parameter
            @RequestParam("language") String language) {

        // Log the received parameters for debugging
        System.out.println("Received answers: " + userAnswers);
        System.out.println("Received word IDs: " + wordIds);

        Map<String, String> answerMap = new HashMap<>();

        // Assuming both lists are of the same size
        if (userAnswers.size() != wordIds.size()) {
            return null;
        }

        // Populate the map with word IDs as keys and user answers as values
        for (int i = 0; i < userAnswers.size(); i++) {
            answerMap.put(wordIds.get(i), userAnswers.get(i));
        }

        List<Words> words = mongoService.getWordsByIds(wordIds);
        int correctCount = 0;
        for (Words word : words) {
            if (word.getWordENG().equalsIgnoreCase(answerMap.get(word.getId()))) {
                correctCount++;
            }

        }

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("score", correctCount + " / " + userAnswers.size());

        return resultData;
    }

}