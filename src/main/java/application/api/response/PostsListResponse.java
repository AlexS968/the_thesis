package application.api.response;

public class PostsListResponse {

    int count;
    private PostResponse[] posts;

    public PostsListResponse() {
    }

    public PostsListResponse(int count, PostResponse[] posts) {
        this.count = count;
        this.posts = posts;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public PostResponse[] getPosts() {
        return posts;
    }

    public void setPosts(PostResponse[] posts) {
        this.posts = posts;
    }
}
