package application.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostResponse {
    private long id;
    private long timestamp;
    private UserPostResponse user;
    private String title;
    private String announce;
    private long likeCount;
    private long dislikeCount;
    private int commentCount;
    private int viewCount;

    public void setUser() {
        this.user = new UserPostResponse();
    }

    public void setUserId(long id) {
        this.user.setId(id);
    }

    public void setUserName(String name) {
        this.user.setName(name);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UserPostResponse {
        private long id;
        private String name;
    }
}

