package com.example.redditclone.controller;

import com.example.redditclone.dto.SubredditDTO;
import com.example.redditclone.service.SubredditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/subreddit")
public class SubredditController {

    private SubredditService subredditService;

    @Autowired
    public SubredditController(SubredditService subredditService) {
        this.subredditService = subredditService;
    }

    @PostMapping
    public ResponseEntity<SubredditDTO> create (@RequestBody SubredditDTO subredditDTO){
        return ResponseEntity.ok(subredditService.create(subredditDTO));
    }

    @GetMapping
    public ResponseEntity<Page<SubredditDTO>> get (Pageable pageable){
        return ResponseEntity.ok(subredditService.get(pageable));
    }
}
