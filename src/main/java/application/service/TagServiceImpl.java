package application.service;

import application.model.Post;
import application.model.Tag;
import application.model.TagToPost;
import application.repository.IPostCount;
import application.repository.PostRepository;
import application.repository.TagRepository;
import application.repository.TagToPostRepository;
import application.service.interfaces.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final TagToPostRepository tagToPostRepository;

    public TagServiceImpl(TagRepository tagRepository, PostRepository postRepository, TagToPostRepository tagToPostRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.tagToPostRepository = tagToPostRepository;
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

    public TagToPost getOrSaveTag(String name, Post post) {
        Tag tag = tagRepository.findByName(name).orElse(null);
        return (tag == null) ? new TagToPost(post, tagRepository.save(new Tag(name))) :
                tagToPostRepository.findByTagIdAndByPostId(tag.getId(), post.getId())
                        .orElse(new TagToPost(post, tag));
    }

    public void deleteAllTagsToPost(Post post) {
        tagToPostRepository.deleteAllByPostId(post.getId());
    }

    @Override
    public void deleteTag(long id) {
        tagRepository.deleteById(id);
    }

    public List<IPostCount> getWeights() {
        return postRepository.countPostsByTag();
    }
}
