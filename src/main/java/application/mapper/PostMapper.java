package application.mapper;

import application.dto.response.*;
import application.model.Post;
import application.model.PostComment;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;

@Service
public class PostMapper {

    public PostsListResponse convertToDto(int offset, int limit, List<Post> posts) {
        int count = posts.size();
        PostsListResponse postsListResponse;
        if (posts != null) {
            postsListResponse = new PostsListResponse(count,
                    posts.subList(offset, Math.min(offset + limit, count)).stream()
                            .map(this::convertToDto).toArray(PostResponse[]::new));
        } else {
            postsListResponse = new PostsListResponse(0, new PostResponse[0]);
        }
        return postsListResponse;
    }

    public PostResponse convertToDto(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.ofHours(1)));
        UserResponse userResponse = new UserResponse();
        userResponse.setId(post.getUser().getId());
        userResponse.setName(post.getUser().getName());
        response.setUser(userResponse);
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
        response.setUser(new UserResponse(post.getUser().getId(), post.getUser().getName())); //UserResponse

        PostCommentResponse[] commentResponses = new PostCommentResponse[comments.size()];
        for (int i = 0; i < comments.size(); i++) {
            commentResponses[i] = new PostCommentResponse(comments.get(i).getId(),
                    comments.get(i).getTime().toEpochSecond(ZoneOffset.ofHours(1)), comments.get(i).getText(),
                    new UserWithPhotoResponse(post.getUser().getId(), post.getUser().getName(), post.getUser().getPhoto()));
        }
        response.setComments(commentResponses);
        response.setTags(tags.toArray(tags.toArray(new String[0])));

        return response;
    }
}
