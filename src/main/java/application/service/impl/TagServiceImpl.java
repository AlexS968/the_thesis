package application.service.impl;

import application.persistence.model.Post;
import application.persistence.model.Tag;
import application.persistence.model.TagToPost;
import application.persistence.repository.TagRepository;
import application.service.TagService;
import application.service.TagToPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagToPostService tagToPostService;

    @Override
    public List<Tag> getTags(String query) {
        return query == null ? tagRepository.findAll()
                : tagRepository.findAllByNameContaining(query);
    }

    @Override
    public List<String> getTagsToPost(long id) {
        return tagRepository.findAllByPost(id);
    }

    @Override
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public TagToPost getOrSaveTag(String name, Post post) {
        Optional<Tag> tag = tagRepository.findByName(name);
        return (tag.isEmpty()) ? new TagToPost(post, tagRepository.save(new Tag(name))) :
                tagToPostService.findByTagIdAndByPostId(tag.get().getId(), post.getId())
                        .orElse(new TagToPost(post, tag.get()));
    }

    @Override
    public void deleteAllTagsToPost(Post post) {
        tagToPostService.deleteAllByPostId(post.getId());
    }

    @Override
    public void deleteTag(long id) {
        tagRepository.deleteById(id);
    }
}
