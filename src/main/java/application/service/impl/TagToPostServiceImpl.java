package application.service.impl;

import application.persistence.model.TagToPost;
import application.persistence.repository.TagToPostRepository;
import application.service.TagToPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagToPostServiceImpl implements TagToPostService {

    private final TagToPostRepository tagToPostRepository;

    @Override
    public Optional<TagToPost> findByTagIdAndByPostId(long tagId, long postId) {
        return tagToPostRepository.findByTagIdAndByPostId(tagId, postId);
    }

    @Override
    public void deleteAllByPostId(long postId) {
        tagToPostRepository.deleteAllByPostId(postId);
    }
}
