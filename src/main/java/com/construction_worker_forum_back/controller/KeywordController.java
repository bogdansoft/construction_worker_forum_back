package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.entity.Keyword;
import com.construction_worker_forum_back.service.KeywordService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin("https://localhost:3000")
@RequestMapping("/api/keyword")
@SecurityRequirement(name = "Bearer Authentication")
@AllArgsConstructor
public class KeywordController {

    private KeywordService keywordService;

    @GetMapping
    public List<Keyword> getAllKeywords(){
        return keywordService.getAllKeywords();
    }
}
