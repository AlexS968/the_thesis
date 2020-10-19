package application.exception.apierror;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiValidationError {
    private String title;
    private String text;
    private String image;
    private String code;
    private String password;
    private String captcha;
    private String email;
    private String name;
    private String photo;

    @Override
    public String toString() {
        return "ApiValidationError{" +
                (title != null ? "title='" + title + '\'' + ", " : "") +
                (text != null ? "text='" + text + '\'' + ", " : "") +
                (image != null ? "image='" + image + '\'' + ", " : "") +
                (code != null ? "code='" + code + '\'' + ", " : "") +
                (password != null ? "password='" + password + '\'' + ", " : "") +
                (captcha != null ? "captcha='" + captcha + '\'' + ", " : "") +
                (email != null ? "email='" + email + '\'' + ", " : "") +
                (name != null ? "name='" + name + '\'' + ", " : "") +
                (photo != null ? "photo='" + photo + '\'' : "") +
                '}';
    }
}
