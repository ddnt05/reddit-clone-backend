package com.example.redditclone.service;

import com.example.redditclone.dto.SubredditDTO;
import com.example.redditclone.model.Subreddit;
import com.example.redditclone.repository.SubredditRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class SubredditService {

    private final SubredditRepository subredditRepository;
    private final ModelMapper subredditMapper = new ModelMapper();

    @Autowired
    public SubredditService(SubredditRepository subredditRepository) {
        this.subredditRepository = subredditRepository;
    }

    public SubredditDTO create(SubredditDTO subredditDTO){
        return toDTO(subredditRepository.save(fromDTO(subredditDTO)));
    }

    public Page<SubredditDTO> get (Pageable pageable){
        return subredditRepository.findAll(pageable).map(this::toDTO);
    }

    public SubredditDTO toDTO(Subreddit subreddit){
        return subredditMapper.map(subreddit,SubredditDTO.class);
    }

    public Subreddit fromDTO(SubredditDTO subredditDTO){
        return subredditMapper.map(subredditDTO,Subreddit.class);
    }
}
