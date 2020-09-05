package application.service.interfaces;

import application.api.request.LikeRequest;
import application.api.request.ModerationRequest;
import application.api.request.PostRequest;
import application.model.Post;
import application.model.User;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

public interface PostService {

    List<Post> getPosts();

    List<Post> getSortedPosts(String mode);

    List<Post> getQueriedPosts(String query);

    List<Post> getPostsByTag(String tag);

    boolean moderatePost(ModerationRequest request, HttpSession session);

    List<Post> getPostsByDate(String date);

    List<Post> getMyPosts(HttpSession session, String status);

    List<Post> getPostsForModeration(HttpSession session, String status);

    int getModerationCounter(HttpSession session);

    Optional<Post> getPostByID(long id);

    Post getPostByLikeRequest(LikeRequest request);

    Post savePost(Post post);

    Post updatePost(long postId, PostRequest request, HttpSession session);

    void deletePost(long id);
}
