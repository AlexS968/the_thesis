package application.api.response.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPostCommentResponse {
    private long id;
    private String name;
    private String photo;
}
