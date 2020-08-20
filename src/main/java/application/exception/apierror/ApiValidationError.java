package application.exception.apierror;

import com.fasterxml.jackson.annotation.JsonInclude;

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

    public ApiValidationError() {
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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
