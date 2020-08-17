package application.mapper;

import application.api.request.PostRequest;
import application.api.response.*;
import application.exception.ApiValidationException;
import application.exception.apierror.ApiValidationError;
import application.model.*;
import application.service.TagServiceImpl;
import org.springframework.stereotype.Service;

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

    public PostMapper(TagServiceImpl tagService) {
        this.tagService = tagService;
    }

    public PostsListResponse convertToDto(int offset, int limit, List<Post> posts, int moderationCounter) {
        PostsListResponse postsListResponse;
        if (posts != null) {
            postsListResponse = new PostsListResponse(moderationCounter,
                    posts.subList(offset, Math.min(offset + limit,
                            posts.size())).stream()
                            .map(this::convertToDto)
                            .toArray(PostResponse[]::new));
        } else {
            postsListResponse = new PostsListResponse(0, new PostResponse[0]);
        }
        return postsListResponse;
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

    public PostByIdResponse convertToDto(Post post, List<String> tags, List<PostComment> comments) {
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

        PostCommentResponse[] commentResponses = new PostCommentResponse[comments.size()];
        for (int i = 0; i < comments.size(); i++) {
            commentResponses[i] = new PostCommentResponse(comments.get(i).getId(),
                    comments.get(i).getTime().toEpochSecond(ZoneOffset.ofHours(1)), comments.get(i).getText(),
                    new PostCommentResponse.UserPostCommentResponse(post.getUser().getId(), post.getUser().getName(), post.getUser().getPhoto()));
        }
        response.setComments(commentResponses);
        response.setTags(tags.toArray(tags.toArray(new String[0])));

        return response;
    }

    public Post convertToEntity(PostRequest request, User user) {
        if (request.getTitle().length() < 3) {
            throw new ApiValidationException(new ApiValidationError(
                    "Заголовок не установлен",null),"");
        }
        if (request.getText().length() < 50) {
            throw new ApiValidationException(new ApiValidationError(
                    null,"Текст публикации слишком короткий"),"");
        }

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
