package application.service.interfaces;

import application.api.request.LikeRequest;
import application.api.request.ModerationRequest;
import application.api.request.PostRequest;
import application.api.response.ResultResponse;
import application.model.Post;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface PostService {

    List<Post> getPosts();

    List<Post> getSortedPosts(String mode);

    List<Post> getQueriedPosts(String query);

    List<Post> getPostsByTag(String tag);

    ResultResponse moderatePost(ModerationRequest request, Principal principal);

    List<Post> getPostsByDate(String date);

    List<Post> getMyPosts(Principal principal, String status);

    List<Post> getPostsForModeration(String email, String status);

    Optional<Post> getPostByID(long id);

    Post getPostByLikeRequest(LikeRequest request);

    ResultResponse savePost(Post post);

    Post updatePost(long postId, PostRequest request, Principal principal);

    void deletePost(long id);
}
