package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.entity.Keyword;
import com.construction_worker_forum_back.repository.KeywordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class KeywordService {

    private KeywordRepository keywordRepository;


    public List<Keyword> getAllKeywords() {
        List<Keyword> allKeywords = new ArrayList<>();
        keywordRepository
                .findAll()
                .forEach(allKeywords::add);
        return allKeywords;
    }
}
