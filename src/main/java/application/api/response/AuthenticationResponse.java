package application.api.response;

import application.api.response.type.UserAuthCheckResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponse extends ResultResponse {
    private UserAuthCheckResponse user;

    public AuthenticationResponse(boolean result, UserAuthCheckResponse user) {
        super(result);
        this.user = user;
    }

    public AuthenticationResponse(boolean result) {
        super(result);
    }
}

