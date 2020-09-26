package application.service;

import application.model.Post;
import application.model.Tag;
import application.model.TagToPost;
import application.model.repository.IPostCount;
import application.model.repository.PostRepository;
import application.model.repository.TagRepository;
import application.model.repository.TagToPostRepository;
import application.service.interfaces.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final TagToPostRepository tagToPostRepository;

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
        Tag tag = tagRepository.findByName(name).orElse(null);
        return (tag == null) ? new TagToPost(post, tagRepository.save(new Tag(name))) :
                tagToPostRepository.findByTagIdAndByPostId(tag.getId(), post.getId())
                        .orElse(new TagToPost(post, tag));
    }

    @Override
    public void deleteAllTagsToPost(Post post) {
        tagToPostRepository.deleteAllByPostId(post.getId());
    }

    @Override
    public void deleteTag(long id) {
        tagRepository.deleteById(id);
    }

    @Override
    public List<IPostCount> getWeights() {
        return postRepository.countPostsByTag();
    }
}
