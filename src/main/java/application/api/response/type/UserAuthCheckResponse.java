package application.api.response.type;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthCheckResponse {
    private long id;
    @NotNull
    @ApiModelProperty(notes= "${UserAuthCheckResponse.name}")
    private String name;
    private String photo;
    private String email;
    @JsonProperty("moderation")
    private boolean isModerator;
    @ApiModelProperty(notes= "${UserAuthCheckResponse.moderationCount}")
    private int moderationCount;
    private boolean settings;
}
