package com.example.redditclone.dto;

import com.example.redditclone.model.Post;
import com.example.redditclone.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubredditDTO {


    private Long id;
    private String name;
    private String description;
    private Integer numberOfPosts;
}
