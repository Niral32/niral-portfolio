package com.niralpatel.portfolio.service;

import com.niralpatel.portfolio.entity.ContactMessage;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Sends an email to Niral whenever a new contact form submission is saved.
 * Failures are logged but do NOT propagate — we never want a flaky SMTP server
 * to make the public POST /api/contact endpoint return 500.
 */
@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z", Locale.US).withZone(ZoneId.systemDefault());

    private final JavaMailSender mailSender;

    @Value("${notifications.email.enabled:false}")
    private boolean enabled;

    @Value("${notifications.email.to:}")
    private String toAddress;

    @Value("${notifications.email.from:}")
    private String fromAddress;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void notifyNewContactMessage(ContactMessage msg) {
        if (!enabled) {
            log.debug("Email notifications disabled — skipping.");
            return;
        }
        if (mailPassword == null || mailPassword.isBlank()) {
            log.warn("MAIL_PASSWORD env var not set — skipping email for contact #{}. " +
                    "See SETUP-EMAIL.md to enable.", msg.getId());
            return;
        }
        if (toAddress == null || toAddress.isBlank()) {
            log.warn("notifications.email.to is empty — skipping email for contact #{}.", msg.getId());
            return;
        }

        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toAddress);
            helper.setReplyTo(msg.getEmail());
            helper.setSubject("[Portfolio] " + msg.getSubject());
            helper.setText(buildBody(msg), false);

            mailSender.send(mime);
            log.info("Sent contact notification email for contact #{} to {}.", msg.getId(), toAddress);
        } catch (Exception ex) {
            log.error("Failed to send notification email for contact #{}: {}", msg.getId(), ex.getMessage(), ex);
        }
    }

    private String buildBody(ContactMessage msg) {
        String when = msg.getCreatedAt() != null ? TS_FMT.format(msg.getCreatedAt()) : "(unknown time)";
        return "New contact form submission on your portfolio site\n" +
                "------------------------------------------------------------\n" +
                "From:    " + msg.getName() + " <" + msg.getEmail() + ">\n" +
                "Subject: " + msg.getSubject() + "\n" +
                "When:    " + when + "\n" +
                "------------------------------------------------------------\n\n" +
                msg.getMessage() + "\n\n" +
                "------------------------------------------------------------\n" +
                "Reply directly to this email to respond — Reply-To is set to " + msg.getEmail() + ".\n";
    }
}
