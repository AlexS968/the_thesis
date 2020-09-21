package application.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentResponse {
    private long id;
    private long timestamp;
    private String text;
    private UserPostCommentResponse user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPostCommentResponse {
        private long id;
        private String name;
        private String photo;
    }
}
