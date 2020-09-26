package application.service.mapper;

import application.api.response.StatisticsResponse;
import application.model.Post;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;

@Service
public class StatisticsMapper {

    public StatisticsResponse convertToDto(List<Post> posts) {
        StatisticsResponse response = new StatisticsResponse();
        response.setPostsCount(posts.size());
        response.setLikeCount(posts.stream().map(Post::getLikesNumber).reduce(0L, Long::sum));
        response.setDislikeCount(posts.stream().map(Post::dislikeVotesNumber).reduce(0L, Long::sum));
        response.setViewCount(posts.stream().map(Post::getViewCount).reduce(0, Integer::sum));
        response.setFirstPublication(posts.get(0).getTime().toEpochSecond(ZoneOffset.ofHours(1)));
        return response;
    }
}
