package application.service;

import application.model.Tag;
import application.repository.IPostCount;
import application.repository.PostRepository;
import application.repository.TagRepository;
import application.service.interfaces.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public TagServiceImpl(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    @Override
    public List<Tag> getTags(String query) {
        return query == null ? tagRepository.findAll()
                : tagRepository.findAllByNameContaining(query);
    }

    public List<String> getTagsToPost(long id) {
        return tagRepository.findAllByPost(id);
    }

    @Override
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public void deleteTag(long id) {
        tagRepository.deleteById(id);
    }

    public List<IPostCount> getWeights() {
        return postRepository.countPostsByTag();
    }
}
