package application.exception.apierror;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
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

    public ApiValidationError(String text) {
        this.text = text;
    }

    public ApiValidationError(String code, String password, String captcha) {
        this.code = code;
        this.password = password;
        this.captcha = captcha;
    }

    public ApiValidationError(String password, String captcha, String email, String name) {
        this.password = password;
        this.captcha = captcha;
        this.email = email;
        this.name = name;
    }

    public ApiValidationError(String title, String text) {
        this.title = title;
        this.text = text;
    }

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
