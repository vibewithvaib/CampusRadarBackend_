package org.campus.campusradarbackend.service;

import org.campus.campusradarbackend.model.InternshipApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${campusradar.frontend.base-url:https://frontend.com}")
    private String frontendBaseUrl;

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendShortlistNotification(String studentEmail, String studentFirstName, String internshipTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(studentEmail);
        message.setSubject("Update on your Internship Application for " + internshipTitle);
        message.setText(
                "Dear " + studentFirstName + ",\n\n" +
                        "Congratulations! We are pleased to inform you that you have been shortlisted for the '" +
                        internshipTitle + "' position  .\n\n" +
                        "The recruiter will be in touch with you regarding the next steps.\n\n" +
                        "Best regards,\nThe CampusRadar Team"
        );
        mailSender.send(message);
    }

    public void sendHiredNotification(String studentEmail, String internshipTitle, String studentFirstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(studentEmail);
        message.setSubject("Update on your Internship Application for " + internshipTitle);
        message.setText("Dear " + studentFirstName + ",\n\n" +
                "Congratulations! We are pleased to inform you that you have been hired for the '" +
                internshipTitle + "' position  .\n\n" +
                "The recruiter will be in touch with you regarding the next steps. and your profile has been blacklisted now.\n\n" +
                "Best regards,\nThe CampusRadar Team");
    }


    @Async
    public void sendRejectionNotification(String studentEmail, String studentFirstName, String internshipTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(studentEmail);
        message.setSubject("Update on your Internship Application for " + internshipTitle);
        message.setText(
                "Dear " + studentFirstName + ",\n\n" +
                        "Thank you for your interest in the '" + internshipTitle + ".\n\n" +
                        "After careful consideration, we have decided to move forward with other candidates at this time. " +
                        "We encourage you to apply for other opportunities on CampusRadar.\n\n" +
                        "Best regards,\nThe CampusRadar Team"
        );
        mailSender.send(message);
    }


}
