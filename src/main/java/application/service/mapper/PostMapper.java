package application.service.mapper;

import application.api.response.PostByIdResponse;
import application.api.response.PostCommentResponse;
import application.api.response.PostResponse;
import application.api.response.PostsListResponse;
import application.api.response.type.UserPostCommentResponse;
import application.api.response.type.UserPostResponse;
import application.persistence.model.Post;
import application.persistence.model.PostComment;
import application.persistence.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;

@Service
public class PostMapper {

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
            Post post, List<String> tags, List<PostComment> comments) {
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
}
