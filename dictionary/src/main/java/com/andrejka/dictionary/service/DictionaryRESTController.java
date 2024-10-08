package com.andrejka.dictionary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andrejka.dictionary.Words;

@RestController
@RequestMapping("/words")
public class DictionaryRESTController {

    @Autowired
    private MongoService mongoService;

    @CrossOrigin(origins = "*")
    @PostMapping("/addWord")
    public void addWord(@RequestParam("wordENG") String wordENG, @RequestParam("wordLT") String wordLT,
            @RequestParam("wordRU") String wordRU, @RequestParam("sentanceWithThisWord") String sentanceWithThisWord) {
        Words word = new Words();
        word.setWordEN(wordENG);
        word.setWordLT(wordLT);
        word.setWordRU(wordRU);
        word.setSentanceWithThisWord(sentanceWithThisWord);
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
}