package application.service.mapper;

import application.api.request.PostRequest;
import application.api.response.PostByIdResponse;
import application.api.response.PostCommentResponse;
import application.api.response.PostResponse;
import application.api.response.PostsListResponse;
import application.api.response.type.UserPostCommentResponse;
import application.api.response.type.UserPostResponse;
import application.exception.ApiValidationException;
import application.exception.apierror.ApiValidationError;
import application.model.Post;
import application.model.PostComment;
import application.model.TagToPost;
import application.model.User;
import application.model.enums.ModerationStatus;
import application.model.repository.PostRepository;
import application.model.repository.UserRepository;
import application.service.TagServiceImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostMapper {
    private final TagServiceImpl tagService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
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
        modelMapper().map(post, response);
        UserPostResponse userResponse = new UserPostResponse();
        modelMapper().map(post.getUser(), userResponse);
        response.setUser(userResponse);
        response.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.ofHours(2)));
        response.setAnnounce(post.getText().substring(0, Math.min(post.getText().length(), 100)) + "...");
        response.setLikeCount(post.getLikesNumber());
        response.setDislikeCount(post.dislikeVotesNumber());
        response.setCommentCount(post.getPostComments().size());
        return response;
    }

    public PostByIdResponse convertToDto(
            Post post, List<String> tags, List<PostComment> comments, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        //increase view counter under certain conditions
        if (user == null || (!user.isModerator() & post.getUser().getId() != user.getId())) {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }
        //create new dto
        PostByIdResponse response = new PostByIdResponse();
        modelMapper().map(post, response);
        UserPostResponse userResponse = new UserPostResponse();
        modelMapper().map(post.getUser(), userResponse);
        response.setUser(userResponse);
        response.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.ofHours(2)));
        response.setLikeCount(post.getLikesNumber());
        response.setDislikeCount(post.dislikeVotesNumber());

        if (comments != null) {
            PostCommentResponse[] commentResponses = new PostCommentResponse[comments.size()];
            for (int i = 0; i < comments.size(); i++) {
                User commentator = comments.get(i).getUser();
                commentResponses[i] = new PostCommentResponse(comments.get(i).getId(),
                        comments.get(i).getTime().toEpochSecond(ZoneOffset.ofHours(2)),
                        comments.get(i).getText(), new UserPostCommentResponse(commentator.getId(),
                        commentator.getName(), commentator.getPhoto()));
            }
            response.setComments(commentResponses);
        }
        response.setTags(tags.toArray(tags.toArray(new String[0])));
        return response;
    }

    public Post convertToEntity(PostRequest request, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
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
        LocalDateTime postTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(request.getTimestamp() * 1000), ZoneId.of("+02:00"));//+02:00
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
