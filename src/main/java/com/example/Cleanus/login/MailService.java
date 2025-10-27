package com.example.Cleanus.login;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Value("${sendgrid.api-key}") private String apiKey;
    @Value("${app.mail.from}")    private String fromEmail;

    public void sendCodeMail(String to, String code, int expMin) throws Exception {
        Email from = new Email(fromEmail, "CleanUs");
        Email toEmail = new Email(to);
        String subject = "이메일 인증코드";
        String body = "인증코드: " + code + " (" + expMin + "분 유효함)";

        Mail mail = new Mail(from, subject, toEmail, new Content("text/plain", body));
        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("auth/send");
        request.setBody(mail.build());

        Response response = sg.api(request);
        if(response.getStatusCode() >= 400) {
            throw new IllegalStateException("Sendgrid Error: " + response.getStatusCode() + " " + response.getBody() );
        }
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());
        System.out.println("Headers: " + response.getHeaders());
    }
}
