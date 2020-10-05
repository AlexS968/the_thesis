package application.service;

import application.api.request.PostCommentRequest;
import application.api.response.CommentResponse;
import application.exception.ApiValidationException;
import application.exception.BadRequestException;
import application.exception.EntNotFoundException;
import application.exception.apierror.ApiValidationError;
import application.model.Post;
import application.model.PostComment;
import application.model.User;
import application.model.repository.PostCommentRepository;
import application.model.repository.PostRepository;
import application.model.repository.UserRepository;
import application.service.interfaces.PostCommentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public List<PostComment> getTagsToPost(long id) {
        return postCommentRepository.findAllByPost(id);
    }

    @Override
    public PostComment getCommentById(long id) {
        return postCommentRepository.findById(id).orElseThrow(EntNotFoundException::new);
    }

    @Override
    public CommentResponse addPostComment(PostCommentRequest request, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        //get post or throw BadRequestException
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(BadRequestException::new);
        //get parent comment, or if comment with this number don't exist, throw BadRequestException
        PostComment parentComment = request.getParentId() != null ? postCommentRepository
                .findById(request.getParentId()).orElseThrow(BadRequestException::new) : null;

        if (request.getParentId() != null) {
            parentComment = postCommentRepository.findById(request.getParentId()).orElseThrow(BadRequestException::new);
        }
        //check comment text
        if (request.getText().length() < 30) {
            throw new ApiValidationException(
                    new ApiValidationError("Text is missing or too short"), "");
        }
        //create and save new comment
        PostComment comment = postCommentRepository.save(new PostComment(
                parentComment, post, user, LocalDateTime.now(), request.getText()));
        return new CommentResponse(comment.getId());
    }
}
