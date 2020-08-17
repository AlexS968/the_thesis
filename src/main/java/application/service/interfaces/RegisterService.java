package application.service.interfaces;

public interface RegisterService {

    void checkAndCreateUser(String email, String name, String code,
                            String secretCode, String password);

    //boolean checkName(String name);

    boolean checkCaptcha(String code, String secretCode);
}
