package application.api.response;

import application.api.response.base.CountDataResponse;
import application.api.response.type.UserPostResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostResponse extends CountDataResponse {
    private UserPostResponse user;
    private String title;
    private String announce;
    private int commentCount;
}

