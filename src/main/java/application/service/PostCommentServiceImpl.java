package application.service;

import application.api.request.PostCommentRequest;
import application.exception.ApiValidationException;
import application.exception.BadRequestException;
import application.exception.EntNotFoundException;
import application.exception.UserUnauthenticatedException;
import application.exception.apierror.ApiValidationError;
import application.model.Post;
import application.model.PostComment;
import application.model.User;
import application.repository.PostCommentRepository;
import application.service.interfaces.PostCommentService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostServiceImpl postService;
    private final UserServiceImpl userService;

    public PostCommentServiceImpl(PostCommentRepository postCommentRepository, PostServiceImpl postService, UserServiceImpl userService) {
        this.postCommentRepository = postCommentRepository;
        this.postService = postService;
        this.userService = userService;
    }

    public List<PostComment> getTagsToPost(long id) {
        return postCommentRepository.findAllByPost(id);
    }

    public PostComment getCommentById(long id) {
        return postCommentRepository.findById(id).isPresent() ?
                postCommentRepository.findById(id).get() : null;
    }

    public long addPostComment(PostCommentRequest request, HttpSession session) {
        //check authentication
        if (LoginServiceImpl.getSessionsId().get(session.getId()) == null) {
            throw new UserUnauthenticatedException();
        }
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntNotFoundException::new);
        //get post or throw BadRequestException
        Post post = postService.getPostByID(request.getPost_id())
                .orElseThrow(BadRequestException::new);
        //get parent comment, or if comment with this number don't exist, throw BadRequestException
        PostComment parentComment = request.getParent_id() != null ? postCommentRepository
                .findById(request.getParent_id()).orElseThrow(BadRequestException::new) : null;

        if (request.getParent_id() != null) {
            parentComment = postCommentRepository.findById(request.getParent_id()).orElseThrow(BadRequestException::new);
        }
        //check comment text
        if (request.getText().length() < 30) {
            throw new ApiValidationException(
                    new ApiValidationError("Text is missing or too short"), "");
        }
        //create and save new comment
        PostComment comment = postCommentRepository.save(new PostComment(
                parentComment, post, user, LocalDateTime.now(), request.getText()));
        return comment.getId();
    }
}
