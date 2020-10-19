package application.service.impl;

import application.api.request.PostCommentRequest;
import application.api.response.CommentResponse;
import application.exception.ApiValidationException;
import application.exception.apierror.ApiValidationError;
import application.persistence.model.Post;
import application.persistence.model.PostComment;
import application.persistence.model.User;
import application.persistence.repository.PostCommentRepository;
import application.service.PostCommentService;
import application.service.PostService;
import application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final UserService userService;
    private final PostService postService;

    @Override
    public List<PostComment> getTagsToPost(long id) {
        return postCommentRepository.findAllByPost(id);
    }

    @Override
    public PostComment getCommentById(long id) {
        return postCommentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public CommentResponse addPostComment(PostCommentRequest request, Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        //get post or throw BadRequestException
        Post post = postService.getPostByID(request.getPostId());
        //get parent comment, or if comment with this number don't exist, throw BadRequestException
        PostComment parentComment = request.getParentId() != null ? postCommentRepository
                .findById(request.getParentId()).orElseThrow(EntityNotFoundException::new) : null;

        if (request.getParentId() != null) {
            parentComment = postCommentRepository.findById(request.getParentId())
                    .orElseThrow(EntityNotFoundException::new);
        }
        //check comment text
        if (request.getText().length() < 30) {
            ApiValidationError apiValidationError = new ApiValidationError();
            apiValidationError.setText("Text is missing or too short");
            throw new ApiValidationException(apiValidationError);
        }
        //create and save new comment
        PostComment comment = postCommentRepository.save(new PostComment(
                parentComment, post, user, LocalDateTime.now(), request.getText()));
        return new CommentResponse(comment.getId());
    }
}
