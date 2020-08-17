package application.service.interfaces;

import application.api.request.PostRequest;
import application.model.Post;
import application.model.User;

import java.util.List;

public interface PostService {

    List<Post> getPosts();

    List<Post> getSortedPosts(String mode);

    List<Post> getQueriedPosts(String query);

    List<Post> getPostsByTag(String tag);

    void moderatePost(long postId, User moderator, String decision);

    List<Post> getPostsByDate(String date);

    List<Post> getMyPosts(User user, String status);

    List<Post> getPostsForModeration(User user, String status);

    int getModerationCounter(User moderator);

    Post getPostByID(long id);

    Post savePost(Post post);

    Post updatePost(long postId, PostRequest request, User user);

    void deletePost(long id);
}
