package application.api.response;

public class RegisterResponse {

    private boolean result;
    private RegisterError errors;

    public RegisterResponse() {
        this.errors = new RegisterError();
    }

    public RegisterResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public RegisterError getErrors() {
        return errors;
    }

    public void setErrorsEmail(String email) {
        this.errors.setEmail(email);
    }

    public void setErrorsName(String name) {
        this.errors.setName(name);
    }

    public void setErrorsPassword(String password) {
        this.errors.setPassword(password);
    }

    public void setErrorsCaptcha(String captcha) {
        this.errors.setCaptcha(captcha);
    }
}

class RegisterError {

    private String email; //"Этот e-mail уже зарегистрирован"
    private String name; //"Имя указано неверно"
    private String password; //"Пароль короче 6-ти символов"
    private String captcha; //"Код с картинки введён неверно"

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
}
