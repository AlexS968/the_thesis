package application.api.response;

public class ChangePasswordResponse {

    private boolean result;
    private ChangePasswordError errors;

    public ChangePasswordResponse() {
        this.errors = new ChangePasswordError();
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ChangePasswordError getErrors() {
        return errors;
    }

    public void setErrorsCode(String code) {
        this.errors.setCode(code);
    }

    public void setErrorsPassword(String password) {
        this.errors.setPassword(password);
    }

    public void setErrorsCaptcha(String captcha) {
        this.errors.setCaptcha(captcha);
    }
}

class ChangePasswordError {

    private String code; /*"Ссылка для восстановления пароля устарела. <a href=\"/auth/restore\">Запросить ссылку снова</a>"*/
    private String password; //"Пароль короче 6-ти символов",
    private String captcha; //"Код с картинки введён неверно"

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
}
