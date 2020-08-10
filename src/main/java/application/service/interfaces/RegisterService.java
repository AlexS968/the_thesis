package application.service.interfaces;

public interface RegisterService {

    boolean checkEmail(String email);

    boolean checkName(String name);

    boolean checkCaptcha(String code, String secretCode);
}
