package application.service.impl;

import application.service.SendGridMailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGridAPI;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SendGridMailServiceImpl implements SendGridMailService {

    private final SendGridAPI sendGrid;

    @Value("${appEmail.email}")
    public String email;

    @Override
    public void sendMail(String addressee, String subject, String text) {
        Email from = new Email(email);
        Email to = new Email(addressee);
        Content content = new Content("text/plain", text);
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            System.out.println(response.getBody());
        } catch (IOException ex) {
            ex.getStackTrace();
        }
    }
}
