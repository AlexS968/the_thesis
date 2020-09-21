package application.exception.apierror;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class ApiError {
    private boolean result;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ApiValidationError errors;

    public ApiError(ApiValidationError errors) {
        this.errors = errors;
    }

    public ApiError(boolean result, ApiValidationError errors) {
        this.result = result;
        this.errors = errors;
    }
}
