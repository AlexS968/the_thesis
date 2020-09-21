package application.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModerationRequest {
    @JsonProperty("post_id")
    private long postId;
    private String decision; //decision on the post: accept или decline
}
