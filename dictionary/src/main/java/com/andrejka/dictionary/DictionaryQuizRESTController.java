package com.andrejka.dictionary;

import java.util.ArrayList;
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
        List<Map<String, String>> randomWords = mongoService.getRandomWords(count, language);
        for (Map<String, String> word : randomWords) {
            String wordENG = word.get("wordENG");
            String sentence = word.get("sentence");
            String modifiedSentence = sentence.replaceAll("(?i)" + wordENG, "_______");
            word.put("sentence", modifiedSentence);
        }

        return randomWords;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/submitResults")
    public Map<String, Object> submitQuizResults(@RequestParam("answers") List<String> userAnswers,
            @RequestParam("wordIds") List<String> wordIds, @RequestParam("language") String language) {

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
        List<Map<String, String>> comparisonResults = new ArrayList<>();

        // Set a threshold for accepting minor spelling errors
        int threshold = 2; // You can adjust this threshold based on your needs

        for (Words word : words) {
            String correctWord = word.getWordENG();
            String userAnswer = answerMap.get(word.getId());

            int distance = levenshteinDistance(correctWord.toLowerCase(), userAnswer.toLowerCase());

            Map<String, String> comparison = new HashMap<>();
            comparison.put("correctWord", correctWord);
            comparison.put("userAnswer", userAnswer);

            if (distance <= threshold) {
                correctCount++;
                comparison.put("status", "Correct");
            } else {
                comparison.put("status", "Incorrect");
            }

            comparisonResults.add(comparison);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("correctCount", correctCount);
        result.put("totalQuestions", words.size());
        result.put("comparisons", comparisonResults);

        return result;
    }

    public int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(dp[i - 1][j] + 1, Math.min(dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost));
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }
}