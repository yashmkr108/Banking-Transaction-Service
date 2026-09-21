package com.yash.banking_transaction_service.service;

import com.yash.banking_transaction_service.dto.TransferCompletedEmailPayload;
import com.yash.banking_transaction_service.entity.OutboxEvent;
import com.yash.banking_transaction_service.exceptions.email.EmailDeliveryException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    public EmailService(
            JavaMailSender mailSender,
            ObjectMapper objectMapper) {
        this.mailSender = mailSender;
        this.objectMapper = objectMapper;
    }

    public void sendTransferCompletedEmail(OutboxEvent event) {

        try{

        TransferCompletedEmailPayload payload = objectMapper
                .readValue(event.getPayload(), TransferCompletedEmailPayload.class);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(payload.destinationEmail());
        message.setSubject("Transfer Completed");


        message.setText(
                "Your transfer " + payload.transferReference()
                        + " has been completed.\n\n"
                        + "Amount: " + payload.amount() + "\n"
                        + "Destination Account: "
                        + payload.destinationAccountNumber()
        );

        mailSender.send(message);
        }catch (MailException e){
            throw new EmailDeliveryException(
                    "Failed to send transfer completion email", e
            );
        }
    }
}