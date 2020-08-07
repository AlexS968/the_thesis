package application.service;

import application.model.Post;

import java.util.List;

public interface PostService {

    List<Post> getPosts();

    List<Post> getSortedPosts(String mode);

    List<Post> getQueriedPosts(String query);

    List<Post> getPostsByTag(String tag);

    List<Post> getPostsByDate(String date);

    Post getPostByID(long id);

    Post savePost(Post post);

    void deletePost(long id);
}
