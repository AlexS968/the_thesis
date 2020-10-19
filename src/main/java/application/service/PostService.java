package application.service;

import application.api.request.LikeRequest;
import application.api.request.ModerationRequest;
import application.api.request.PostRequest;
import application.api.response.ResultResponse;
import application.persistence.model.Post;
import application.persistence.repository.IPostCount;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface PostService {

    List<Post> getSortedPosts(String mode);

    List<Post> getQueriedPosts(String query);

    List<Post> getPostsByTag(String tag);

    ResultResponse moderatePost(ModerationRequest request, Principal principal);

    List<Post> getPostsByDate(String date);

    List<Post> getMyPosts(Principal principal, String status);

    List<Post> getPostsForModeration(String email, String status);

    Post getPostByID(long id);

    Post getPostByLikeRequest(LikeRequest request);

    ResultResponse createNewPost(PostRequest request, Principal principal);

    ResultResponse updatePost(long postId, PostRequest request, Principal principal);

    void increaseViewCounter(long id, Principal principal);

    Optional<Post> findEarliestPost();

    List<IPostCount> countPostsByDay(Timestamp from, Timestamp to);

    List<IPostCount> countPostsByTag();

    List<Post> findAllOrderByTimeAsc();

    List<Post> findAllByUserIdOrderByTimeAsc(long userId);
}
