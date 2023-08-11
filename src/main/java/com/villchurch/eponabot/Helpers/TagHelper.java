package com.villchurch.eponabot.Helpers;

import com.villchurch.eponabot.Repositories.TagRepository;
import com.villchurch.eponabot.models.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TagHelper {

    @Autowired
    TagRepository getTagRepository;

    private static TagRepository tagRepository;

    @PostConstruct
    private void init() {
        tagRepository = getTagRepository;
    }

    public static void saveTag(Tag tag) {
        tagRepository.save(tag);
    }

    public static void deleteTag(Tag tag) {
        tagRepository.delete(tag);
    }

    public static Optional<Tag> returnTag(String name) {
        return tagRepository.findTagByTag(name).stream().findFirst();
    }

    public static List<Tag> returnAllTags() {
        return tagRepository.findAll();
    }
}
