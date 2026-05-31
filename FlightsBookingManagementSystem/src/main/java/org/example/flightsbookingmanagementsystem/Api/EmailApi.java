package org.example.flightsbookingmanagementsystem.Api;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailApi {

    private final JavaMailSender javaMailSender;

    public void sendEmail(String recipient, String subject, String message){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom("ammar.alabdullah.1422@gmail.com");
        simpleMailMessage.setTo(recipient);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        javaMailSender.send(simpleMailMessage);

    }


    public void sendEmailWithAttachments(String to, String subject, String htmlBody, byte[] invoicePdf, byte[] ticketPdf) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            helper.addAttachment("Purchase_Invoice.pdf", new ByteArrayResource(invoicePdf));
            helper.addAttachment("Flight_Ticket.pdf", new ByteArrayResource(ticketPdf));

            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new ApiException("Failed to send the email with attachments: " + e.getMessage());
        }
    }


    public void sendEmailWithSingleAttachment(String to, String subject, String htmlBody, byte[] pdfDocument, String fileName) {
        try {
            jakarta.mail.internet.MimeMessage message = javaMailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            helper.addAttachment(fileName, new org.springframework.core.io.ByteArrayResource(pdfDocument));

            javaMailSender.send(message);

        } catch (jakarta.mail.MessagingException e) {
            throw new org.example.flightsbookingmanagementsystem.Api.ApiException("Failed to send the email with attachment: " + e.getMessage());
        }
    }
}







