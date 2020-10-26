package application.api.response.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthCheckResponse {
    private long id;
    private String name;
    private String photo;
    private String email;
    @JsonProperty("moderation")
    private boolean isModerator;
    private int moderationCount;
    private boolean settings;
}
