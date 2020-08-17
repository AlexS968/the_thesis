package application.service;

import application.model.PostComment;
import application.repository.PostCommentRepository;
import application.service.interfaces.PostCommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentRepository postCommentRepository;

    public PostCommentServiceImpl(PostCommentRepository postCommentRepository) {
        this.postCommentRepository = postCommentRepository;
    }

    public List<PostComment> getTagsToPost(long id) {
        return postCommentRepository.findAllByPost(id);
    }

    public PostComment getCommentById(long id) {
        return postCommentRepository.findById(id).isPresent() ?
                postCommentRepository.findById(id).get() : null;
    }

    public long addPostComment(PostComment comment) {
        return postCommentRepository.save(comment).getId();
    }
}
