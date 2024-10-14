package com.andrejka.dictionary;

import java.util.List;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andrejka.dictionary.service.MongoService;

@RestController
@RequestMapping("/words")
public class DictionaryWordsRESTController {

    @Autowired
    private MongoService mongoService;

    @CrossOrigin(origins = "*")
    @PostMapping("/addWord")
    public void addWord(@RequestParam("wordENG") String wordENG, @RequestParam("wordLT") String wordLT,
            @RequestParam("wordRU") String wordRU, @RequestParam("sentence") String sentence) {
        Words word = new Words();
        word.setWordENG(wordENG);
        word.setWordLT(wordLT);
        word.setWordRU(wordRU);
        word.setSentence(sentence);
        mongoService.saveWord(word);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getAllWords")
    public List<Words> getAllWords() {
        return mongoService.getAllWords();
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/deleteWord/{id}")
    public void deleteWord(@PathVariable String id) {
        mongoService.deleteWord(id);
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/updateWord/{id}")
    public ResponseEntity<String> updateWord(@PathVariable String id, @RequestParam(required = false) String wordENG,
            @RequestParam(required = false) String wordLT, @RequestParam(required = false) String wordRU,
            @RequestParam(required = false) String sentence) {

        Words word = mongoService.findWordById(id);
        if (word == null) {
            return ResponseEntity.notFound().build();
        }

        // Update only the fields that are provided
        if (wordENG != null && !wordENG.trim().isEmpty()) {
            word.setWordENG(wordENG);
        }
        if (wordLT != null && !wordLT.trim().isEmpty()) {
            word.setWordLT(wordLT);
        }
        if (wordRU != null && !wordRU.trim().isEmpty()) {
            word.setWordRU(wordRU);
        }
        if (sentence != null && !sentence.trim().isEmpty()) {
            word.setSentence(sentence);
        }

        try {
            mongoService.updateWord(word);
            return ResponseEntity.ok("Word updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Error updating the word.");
        }
    }

}