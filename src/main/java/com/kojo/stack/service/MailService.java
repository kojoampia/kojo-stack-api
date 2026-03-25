package com.kojo.stack.service;

import com.kojo.stack.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails.
 */
@Service
public class MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;

    private final String mailFrom;

    private final String baseUrl;

    public MailService(
        ObjectProvider<JavaMailSender> mailSenderProvider,
        @Value("${app.mail.from:no-reply@kojo-stack.local}") String mailFrom,
        @Value("${app.mail.base-url:http://localhost:4200}") String baseUrl
    ) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.mailFrom = mailFrom;
        this.baseUrl = baseUrl;
    }

    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        if (to == null || to.isBlank()) {
            LOG.debug("Skipping email send because recipient is blank");
            return;
        }

        if (mailSender == null) {
            LOG.info("Skipping email send because no JavaMailSender bean is configured. to='{}', subject='{}'", to, subject);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(mailFrom);
        message.setSubject(subject);
        message.setText(content);

        try {
            mailSender.send(message);
            LOG.info("Sent email to '{}' with subject '{}'", to, subject);
        } catch (MailException ex) {
            // Keep auth flows resilient even when SMTP is unavailable.
            LOG.warn("Failed to send email to '{}' with subject '{}'", to, subject, ex);
        }
    }

    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        if (user == null || user.getEmail() == null) {
            LOG.debug("Skipping templated email because user or user email is missing");
            return;
        }

        String subject;
        String body;

        if ("mail/activationEmail".equals(templateName)) {
            subject = "Activate your Kojo Stack account";
            body = buildActivationEmailBody(user);
        } else if ("mail/passwordResetEmail".equals(templateName)) {
            subject = "Reset your Kojo Stack password";
            body = buildResetEmailBody(user);
        } else if ("mail/creationEmail".equals(templateName)) {
            subject = "Your Kojo Stack account was created";
            body = buildCreationEmailBody(user);
        } else {
            subject = titleKey;
            body = "This is an automated notification from Kojo Stack.";
        }

        sendEmail(user.getEmail(), subject, body, false, false);
    }

    public void sendActivationEmail(User user) {
        LOG.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/activationEmail", "email.activation.title");
    }

    public void sendCreationEmail(User user) {
        LOG.debug("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/creationEmail", "email.activation.title");
    }

    public void sendPasswordResetMail(User user) {
        LOG.debug("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/passwordResetEmail", "email.reset.title");
    }

    private String buildActivationEmailBody(User user) {
        String activationKey = user.getActivationKey() == null ? "" : user.getActivationKey();
        String activationUrl = baseUrl + "/activate?key=" + activationKey;
        return "Hello " + safeName(user) + ",\n\n"
            + "Welcome to Kojo Stack. Please activate your account using the link below:\n"
            + activationUrl + "\n\n"
            + "If you did not create this account, you can ignore this message.";
    }

    private String buildResetEmailBody(User user) {
        String resetKey = user.getResetKey() == null ? "" : user.getResetKey();
        String resetUrl = baseUrl + "/reset-password?key=" + resetKey;
        return "Hello " + safeName(user) + ",\n\n"
            + "A password reset was requested for your Kojo Stack account. Use the link below:\n"
            + resetUrl + "\n\n"
            + "If you did not request this reset, you can ignore this message.";
    }

    private String buildCreationEmailBody(User user) {
        return "Hello " + safeName(user) + ",\n\n"
            + "Your Kojo Stack account has been created successfully.\n"
            + "You can sign in at: " + baseUrl + "\n";
    }

    private String safeName(User user) {
        String firstName = user.getFirstName();
        if (firstName == null || firstName.isBlank()) {
            return user.getLogin() == null ? "there" : user.getLogin();
        }
        return firstName;
    }
}
