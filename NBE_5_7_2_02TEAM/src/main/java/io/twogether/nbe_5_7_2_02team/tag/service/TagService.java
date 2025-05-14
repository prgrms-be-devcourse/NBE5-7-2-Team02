package io.twogether.nbe_5_7_2_02team.tag.service;

import io.twogether.nbe_5_7_2_02team.tag.dao.TagRepository;
import io.twogether.nbe_5_7_2_02team.tag.domain.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
}
