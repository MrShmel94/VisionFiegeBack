package com.example.ws.microservices.firstmicroservices.utils;

import com.example.ws.microservices.firstmicroservices.customError.EmailSendException;
import com.example.ws.microservices.firstmicroservices.entity.UserEntity;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EmailService {

    private final MailjetClient mailjetClient;

    public void sendVerificationEmail(UserEntity userEntity) {
        String subject = "Verify Your Email - Your Company Name";
        String verifyURL = "http://localhost:8080/api/v1/users/verify?code=" + userEntity.getEmailVerificationToken();

        String mailContent = "<p>Dear " + userEntity.getFirstName() + ",</p>"
                + "<p>Welcome to <strong>Your Company Name</strong>!</p>"
                + "<p>To activate your account and access all features, please confirm your email by clicking the link below:</p>"
                + "<p style='text-align: center;'><a href=\"" + verifyURL + "\" style='background-color:#4CAF50;color:white;padding:10px 20px;text-decoration:none;'>Verify Your Email</a></p>"
                + "<p>If you did not register at our site, please ignore this message.</p>"
                + "<p>Best regards,<br>Your Company Name Team</p>";

        try {
            sendMailjetEmail(userEntity.getEmail(), subject, mailContent);
        } catch (MailjetException e) {
            throw new EmailSendException("Failed to send verification email to " + userEntity.getEmail(), e);
        }
    }

    private void sendMailjetEmail(String recipientEmail, String subject, String content) throws MailjetException {
        MailjetRequest request;
        MailjetResponse response;

        JSONObject message = new JSONObject()
                .put(Emailv31.Message.FROM, new JSONObject()
                        .put("Email", "mrshmel94@gmail.com")
                        .put("Name", "Vision"))
                .put(Emailv31.Message.TO, new JSONArray()
                        .put(new JSONObject()
                                .put("Email", recipientEmail)))
                .put(Emailv31.Message.SUBJECT, subject)
                .put(Emailv31.Message.HTMLPART, content);

        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray().put(message));

        response = mailjetClient.post(request);

        System.out.println(response.getData());

        if (response.getStatus() != 200) {
            throw new MailjetException("Failed to send email: " + response.getStatus() + " - " + response.getData());
        }
    }
}
