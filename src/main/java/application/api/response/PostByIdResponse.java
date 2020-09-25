package application.api.response;

import application.api.response.base.CountDataResponse;
import application.api.response.type.UserPostResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostByIdResponse extends CountDataResponse {
    @JsonProperty("active")
    private boolean isActive;
    private UserPostResponse user;
    private String title;
    private String text;
    private PostCommentResponse[] comments;
    private String[] tags;
}
