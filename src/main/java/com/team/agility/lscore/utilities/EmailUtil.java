package com.team.agility.lscore.utilities;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.RandomStringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EmailUtil {
    private static final String username = "testlms030@gmail.com";
    private static final String password = "gywrcwpzczlpxbco";


    public String generateRandomPassword() {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(2);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
            .concat(numbers)
            .concat(specialChar)
            .concat(totalChars);
            
        List<Character> pwdChars = combinedChars.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toList());

        Collections.shuffle(pwdChars);
        String password = pwdChars.stream()
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();

        return password;
    }


    public void sendPasswordResetEmail(List<String> recipients) {
        Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS
        
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {

            String recipientString = String.join(", ", recipients);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("testlms030@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientString)
            );
            message.setSubject("Password Reset for Library System");
            message.setText("Your Password has been reset to default password");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendPasswordResetEmail(Map<String, String> recipients) {
        Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS
        
        Session session = Session.getInstance(prop,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

        for (Map.Entry<String, String> recipient : recipients.entrySet()) {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("testlms030@gmail.com"));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(recipient.getKey())
                );
                message.setSubject("Password Reset for Library System");
                message.setText("Your Password has been reset to " + recipient.getValue());

                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
}
