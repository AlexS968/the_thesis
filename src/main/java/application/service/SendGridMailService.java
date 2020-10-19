package application.service;

public interface SendGridMailService {

    void sendMail(String addressee, String subject, String text);
}
