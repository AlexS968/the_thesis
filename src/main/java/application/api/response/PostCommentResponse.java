package application.api.response;

import application.api.response.base.PostDataResponse;
import application.api.response.type.UserPostCommentResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostCommentResponse extends PostDataResponse {
    private String text;
    private UserPostCommentResponse user;

    public PostCommentResponse(long id, long timestamp, String text, UserPostCommentResponse user) {
        super(id, timestamp);
        this.text = text;
        this.user = user;
    }
}
