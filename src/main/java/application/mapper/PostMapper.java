package application.mapper;

import application.api.request.PostRequest;
import application.api.response.PostByIdResponse;
import application.api.response.PostCommentResponse;
import application.api.response.PostResponse;
import application.api.response.PostsListResponse;
import application.exception.ApiValidationException;
import application.exception.EntNotFoundException;
import application.exception.UserUnauthenticatedException;
import application.exception.apierror.ApiValidationError;
import application.model.*;
import application.repository.PostRepository;
import application.service.LoginServiceImpl;
import application.service.TagServiceImpl;
import application.service.UserServiceImpl;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PostMapper {

    private final TagServiceImpl tagService;
    private final UserServiceImpl userService;
    private final PostRepository postRepository;

    public PostMapper(TagServiceImpl tagService, UserServiceImpl userService, PostRepository postRepository) {
        this.tagService = tagService;
        this.userService = userService;
        this.postRepository = postRepository;
    }

    public PostsListResponse convertToDto(int offset, int limit, List<Post> posts) {
        if (posts != null) {
            return new PostsListResponse(posts.size(),
                    posts.subList(offset, Math.min(offset + limit, posts.size())).stream()
                            .map(this::convertToDto).toArray(PostResponse[]::new));
        } else {
            return new PostsListResponse(0, new PostResponse[0]);
        }
    }

    public PostResponse convertToDto(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.ofHours(1)));
        response.setUser();
        response.setUserId(post.getUser().getId());
        response.setUserName(post.getUser().getName());
        response.setTitle(post.getTitle());
        response.setAnnounce(post.getText().substring(0, Math.min(post.getText().length(), 100)) + "...");
        response.setLikeCount(post.getLikes());
        response.setDislikeCount(post.dislikeVotes());
        response.setCommentCount(post.getPostComments().size());
        response.setViewCount(post.getViewCount());
        return response;
    }

    public PostByIdResponse convertToDto(
            Post post, List<String> tags, List<PostComment> comments, HttpSession session) {
        Long userId = LoginServiceImpl.getSessionsId().get(session.getId());
        User user = (userId != null) ? userService.findUserById(userId)
                .orElseThrow(EntNotFoundException::new) : null;
        //increase view counter under certain conditions
        if (userId == null || (!user.isModerator() & post.getUser().getId() != userId)) {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }
        //create new dto
        PostByIdResponse response = new PostByIdResponse();
        response.setId(post.getId());
        response.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.ofHours(1)));
        response.setActive(post.isActive());
        response.setTitle(post.getTitle());
        response.setText(post.getText());
        response.setLikeCount(post.getLikes());
        response.setDislikeCount(post.dislikeVotes());
        response.setViewCount(post.getViewCount());
        response.setUser();
        response.setUserId(post.getUser().getId());
        response.setUserName(post.getUser().getName());

        if (comments != null) {
            PostCommentResponse[] commentResponses = new PostCommentResponse[comments.size()];
            for (int i = 0; i < comments.size(); i++) {
                commentResponses[i] = new PostCommentResponse(comments.get(i).getId(),
                        comments.get(i).getTime().toEpochSecond(ZoneOffset.ofHours(1)),
                        comments.get(i).getText(), new PostCommentResponse
                        .UserPostCommentResponse(post.getUser().getId(),
                        post.getUser().getName(), post.getUser().getPhoto()));
            }
            response.setComments(commentResponses);
        }
        response.setTags(tags.toArray(tags.toArray(new String[0])));
        return response;
    }

    public Post convertToEntity(PostRequest request, HttpSession session) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(UserUnauthenticatedException::new);
        //check post title and text
        ApiValidationError apiValidationError = new ApiValidationError();
        boolean throwException = false;
        if (request.getTitle().length() < 3) {
            apiValidationError.setTitle("Title is too short");
            throwException = true;
        }
        if (request.getText().length() < 50) {
            apiValidationError.setText("Text is too short");
            throwException = true;
        }
        if (throwException) {
            throw new ApiValidationException(apiValidationError, "");
        }
        //if everything is ok, create new post
        Post post = new Post();
        post.setActive(true);
        post.setModerationStatus(ModerationStatus.NEW);
        post.setTitle(request.getTitle());
        post.setText(request.getText());
        post.setUser(user);
        //checking post time
        LocalDateTime postTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(request.getTimestamp()), ZoneId.systemDefault());
        postTime = postTime.isBefore(LocalDateTime.now()) ? LocalDateTime.now() : postTime;
        post.setTime(postTime);
        //setting tags to post
        Set<TagToPost> tagToPosts = new HashSet<>();
        for (String tag : request.getTags()) {
            tagToPosts.add(tagService.getOrSaveTag(tag, post));
        }
        post.setTagToPosts(tagToPosts);
        return post;
    }
}
