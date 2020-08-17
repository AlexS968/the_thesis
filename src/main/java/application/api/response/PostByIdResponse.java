package application.api.response;

public class PostByIdResponse {

    private long id;
    private long timestamp;
    private boolean active;
    private PostResponse.UserPostResponse user;
    private String title;
    //Текст поста в формате HTML
    private String text;
    private long likeCount;
    private long dislikeCount;
    private int viewCount;
    private PostCommentResponse[] comments;
    private String[] tags;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public PostResponse.UserPostResponse getUser() {
        return user;
    }

    public void setUser() {
        this.user = new PostResponse.UserPostResponse();
    }

    public void setUserId(long id){
        this.user.setId(id);
    }

    public void setUserName(String name){
        this.user.setName(name);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(long dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public PostCommentResponse[] getComments() {
        return comments;
    }

    public void setComments(PostCommentResponse[] comments) {
        this.comments = comments;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
