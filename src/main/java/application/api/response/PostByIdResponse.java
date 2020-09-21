package application.api.response;

import lombok.Data;

@Data
public class PostByIdResponse {
    private long id;
    private long timestamp;
    private boolean active;
    private PostResponse.UserPostResponse user;
    private String title;
    private String text;
    private long likeCount;
    private long dislikeCount;
    private int viewCount;
    private PostCommentResponse[] comments;
    private String[] tags;

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
}
