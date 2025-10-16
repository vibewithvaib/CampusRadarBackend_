package org.campus.campusradarbackend.service;

import org.campus.campusradarbackend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendShortlistNotification(String studentEmail, String studentFirstName, String internshipTitle, String companyName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(studentEmail);
        message.setSubject("Update on your Internship Application for " + internshipTitle);
        message.setText(
                "Dear " + studentFirstName + ",\n\n" +
                        "Congratulations! We are pleased to inform you that you have been shortlisted for the '" +
                        internshipTitle + "' position at " + companyName + ".\n\n" +
                        "The recruiter will be in touch with you regarding the next steps.\n\n" +
                        "Best regards,\nThe CampusRadar Team"
        );
        mailSender.send(message);
    }

    @Async
    public void sendHiredNotification(String studentEmail, String studentFirstName, String internshipTitle, String companyName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(studentEmail);
        message.setSubject("Congratulations! You've been Hired for the " + internshipTitle + " position!");
        message.setText("Dear " + studentFirstName + ",\n\n" +
                "Excellent news! We are thrilled to inform you that " + companyName + " has selected you for the '" +
                internshipTitle + "' position.\n\n" +
                "The recruiter will contact you shortly with the official offer letter and onboarding details.\n\n" +
                "Best regards,\nThe CampusRadar Team");
        mailSender.send(message);
    }


    @Async
    public void sendRejectionNotification(String studentEmail, String studentFirstName, String internshipTitle, String companyName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(studentEmail);
        message.setSubject("Update on your Internship Application for " + internshipTitle);
        message.setText(
                "Dear " + studentFirstName + ",\n\n" +
                        "Thank you for your interest in the '" + internshipTitle + "' position at " + companyName + ".\n\n" +
                        "After careful consideration, the hiring team has decided to move forward with other candidates at this time. " +
                        "We appreciate you taking the time to apply and encourage you to explore other opportunities on CampusRadar.\n\n" +
                        "Best regards,\nThe CampusRadar Team"
        );
        mailSender.send(message);
    }

    @Async
    public void sendAccountApprovalNotification(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Your CampusRadar Account has been Approved!");
        message.setText(
                "Dear " + user.getFirstName() + ",\n\n" +
                        "Welcome to CampusRadar! We are pleased to inform you that your account has been reviewed and approved by an administrator.\n\n" +
                        "You can now log in to your account and start exploring opportunities.\n\n" +
                        "Best regards,\nThe CampusRadar Team"
        );
        mailSender.send(message);
    }
}

