package application.dto.response;

public class PostCommentResponse {

    private long id;
    private long timestamp;
    //Текст поста в формате HTML
    private String text;
    private UserWithPhotoResponse user;

    public PostCommentResponse() {
    }

    public PostCommentResponse(long id, long timestamp, String text, UserWithPhotoResponse user) {
        this.id = id;
        this.timestamp = timestamp;
        this.text = text;
        this.user = user;
    }

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UserWithPhotoResponse getUser() {
        return user;
    }

    public void setUser(UserWithPhotoResponse user) {
        this.user = user;
    }
}
