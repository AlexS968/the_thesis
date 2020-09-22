package application.service.interfaces;

public interface SendGridMailService {

    void sendMail(String addressee, String subject, String text);
}
