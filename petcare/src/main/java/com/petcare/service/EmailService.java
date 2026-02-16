package com.petcare.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petcare.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for sending role-aware HTML emails with different templates
 * Each email type has a dedicated method with its own HTML template
 * 
 * Features:
 * - Asynchronous email sending (non-blocking)
 * - Multiple HTML templates for different email types
 * - Dynamic content rendering using template placeholders
 * - Comprehensive error handling and logging
 * - Role-aware email variants
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@smartpetcare.com}")
    private String fromEmail;

    @Value("${app.mail.from-name:Smart Pet Care}")
    private String fromName;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${notifications.enabled:false}")
    private boolean notificationsEnabled;

    private static final String TEMPLATES_PATH = "classpath:email-templates/";

    /**
     * Legacy method for backwards compatibility
     * Sends plain text email
     */
    public void sendEmail(String to, String subject, String body) {
        if (!notificationsEnabled) {
            log.info("Email skipped (notifications disabled): {} - {}", to, subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(String.format("%s <%s>", fromName, fromEmail));
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {} with subject {}", to, subject);
        } catch (Exception ex) {
            log.warn("Failed to send email to {}: {}", to, ex.getMessage());
        }
    }

    /**
     * Send Registration Success Email
     * Separate role-aware variants for Pet Owner and Veterinary Doctor
     */
    @Async
    public void sendRegistrationSuccessEmail(RegistrationEmailDTO emailData) {
        if (!notificationsEnabled) {
            log.info("Registration email skipped (notifications disabled)");
            return;
        }
        try {
            String templateName = "registration-success.html";
            String htmlContent = loadTemplate(templateName);
            
            // Populate template with data
            htmlContent = populateTemplate(htmlContent, emailData);
            
            sendHtmlEmail(
                emailData.getRecipientEmail(),
                "Welcome to Smart Pet Care - Account Created Successfully",
                htmlContent
            );
            
            log.info("Registration success email sent to: {}", emailData.getRecipientEmail());
        } catch (Exception e) {
            log.error("Error sending registration success email to {}: {}", 
                emailData.getRecipientEmail(), e.getMessage(), e);
        }
    }

    /**
     * Send Appointment Booked Email
     * Separate variants for Pet Owner (confirmation) and Doctor (new appointment notification)
     */
    @Async
    public void sendAppointmentBookedEmail(AppointmentEmailDTO emailData) {
        if (!notificationsEnabled) {
            log.info("Appointment email skipped (notifications disabled)");
            return;
        }
        try {
            String templateName = "appointment-booked.html";
            String htmlContent = loadTemplate(templateName);
            
            // Populate template with appointment data
            htmlContent = populateTemplate(htmlContent, emailData);
            
            String subject = emailData.isPetOwner() 
                ? "Appointment Request Submitted - Awaiting Doctor Approval"
                : "New Appointment Request - Action Required";
            
            sendHtmlEmail(emailData.isPetOwner() 
                ? emailData.getPetOwnerEmail() 
                : emailData.getDoctorEmail(),
                subject,
                htmlContent
            );
            
            log.info("Appointment booked email sent to: {}", 
                emailData.isPetOwner() ? emailData.getPetOwnerEmail() : emailData.getDoctorEmail());
        } catch (Exception e) {
            log.error("Error sending appointment booked email: {}", e.getMessage(), e);
        }
    }

    /**
     * Send Appointment Status Update Email
     * Notifies Pet Owner when doctor approves/rejects/completes appointment
     */
    @Async
    public void sendAppointmentStatusUpdateEmail(AppointmentEmailDTO emailData) {
        if (!notificationsEnabled) {
            log.info("Appointment status email skipped (notifications disabled)");
            return;
        }
        try {
            String templateName = "appointment-status-update.html";
            String htmlContent = loadTemplate(templateName);
            
            // Populate template with updated status
            htmlContent = populateTemplate(htmlContent, emailData);
            
            String subject = buildStatusUpdateSubject(emailData);
            
            sendHtmlEmail(
                emailData.getPetOwnerEmail(),
                subject,
                htmlContent
            );
            
            log.info("Appointment status update email sent to: {}", emailData.getPetOwnerEmail());
        } catch (Exception e) {
            log.error("Error sending appointment status update email: {}", e.getMessage(), e);
        }
    }

    /**
     * Send Payment Success Email
     * Handles both appointment and order payments with separate content
     */
    @Async
    public void sendPaymentSuccessEmail(PaymentEmailDTO emailData) {
        if (!notificationsEnabled) {
            log.info("Payment email skipped (notifications disabled)");
            return;
        }
        try {
            String templateName = "payment-success.html";
            String htmlContent = loadTemplate(templateName);
            
            // Populate template with payment data
            htmlContent = populateTemplate(htmlContent, emailData);
            
            String subject = "Payment Confirmed - Receipt #" + emailData.getTransactionId();
            
            sendHtmlEmail(
                emailData.getRecipientEmail(),
                subject,
                htmlContent
            );
            
            log.info("Payment success email sent to: {}", emailData.getRecipientEmail());
        } catch (Exception e) {
            log.error("Error sending payment success email: {}", e.getMessage(), e);
        }
    }

    /**
     * Send Marketplace Order Placed Email
     * Notifies Pet Owner that order has been confirmed and is being processed
     */
    @Async
    public void sendMarketplaceOrderEmail(OrderEmailDTO emailData) {
        if (!notificationsEnabled) {
            log.info("Marketplace order email skipped (notifications disabled)");
            return;
        }
        try {
            String templateName = "order-placed.html";
            String htmlContent = loadTemplate(templateName);
            
            // Populate template with order data
            htmlContent = populateTemplate(htmlContent, emailData);
            
            sendHtmlEmail(
                emailData.getRecipientEmail(),
                "Order Confirmed - Order #" + emailData.getOrderId(),
                htmlContent
            );
            
            log.info("Marketplace order email sent to: {}", emailData.getRecipientEmail());
        } catch (Exception e) {
            log.error("Error sending marketplace order email: {}", e.getMessage(), e);
        }
    }

    /**
     * Send Order Status Update Email
     * Notifies Pet Owner when order status changes (processing, shipped, out for delivery, delivered)
     */
    @Async
    public void sendOrderStatusUpdateEmail(OrderEmailDTO emailData) {
        if (!notificationsEnabled) {
            log.info("Order status update email skipped (notifications disabled)");
            return;
        }
        try {
            String templateName = "order-status-update.html";
            String htmlContent = loadTemplate(templateName);
            
            // Populate template with order status
            htmlContent = populateTemplate(htmlContent, emailData);
            
            String subject = "Order Update - Order #" + emailData.getOrderId() + " - " + emailData.getOrderStatus();
            
            sendHtmlEmail(
                emailData.getRecipientEmail(),
                subject,
                htmlContent
            );
            
            log.info("Order status update email sent to: {}", emailData.getRecipientEmail());
        } catch (Exception e) {
            log.error("Error sending order status update email: {}", e.getMessage(), e);
        }
    }

    /**
     * Send Doctor Verification Email
     * Notifies veterinary doctor of profile verification result (approved/rejected)
     */
    @Async
    public void sendDoctorVerificationEmail(DoctorVerificationEmailDTO emailData) {
        if (!notificationsEnabled) {
            log.info("Doctor verification email skipped (notifications disabled)");
            return;
        }
        try {
            String templateName = "doctor-verification.html";
            String htmlContent = loadTemplate(templateName);
            
            // Populate template with verification result
            htmlContent = populateTemplate(htmlContent, emailData);
            
            String subject = emailData.isApproved() 
                ? "Profile Verified - Welcome to Smart Pet Care!"
                : "Profile Verification Update - Action Required";
            
            sendHtmlEmail(
                emailData.getRecipientEmail(),
                subject,
                htmlContent
            );
            
            log.info("Doctor verification email sent to: {}", emailData.getRecipientEmail());
        } catch (Exception e) {
            log.error("Error sending doctor verification email: {}", e.getMessage(), e);
        }
    }

    /**
     * Load HTML template from resources
     */
    private String loadTemplate(String templateName) throws IOException {
        String templatePath = "src/main/resources/email-templates/" + templateName;
        return new String(Files.readAllBytes(Paths.get(templatePath)), StandardCharsets.UTF_8);
    }

    /**
     * Populate template with dynamic content using simple placeholder replacement
     * Supports both {{variable}} and conditional logic {{#condition}}...{{/condition}}
     */
    private String populateTemplate(String template, Object data) {
        try {
            // Convert data to map for easier manipulation
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> dataMap = mapper.convertValue(data, Map.class);
            
            // Add frontend URL to all templates
            dataMap.putIfAbsent("loginUrl", frontendUrl + "/login");
            dataMap.putIfAbsent("dashboardUrl", frontendUrl + "/dashboard");
            dataMap.putIfAbsent("trackingUrl", frontendUrl + "/orders/track");
            dataMap.putIfAbsent("helpUrl", frontendUrl + "/help");
            dataMap.putIfAbsent("privacyUrl", frontendUrl + "/privacy");
            dataMap.putIfAbsent("termsUrl", frontendUrl + "/terms");
            
            String result = template;
            
            // Simple placeholder replacement for {{variable}} pattern
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                result = result.replace(placeholder, value);
            }
            
            // Handle conditional blocks {{#condition}}content{{/condition}}
            result = handleConditionalBlocks(result, dataMap);
            
            return result;
        } catch (Exception e) {
            log.error("Error populating template: {}", e.getMessage(), e);
            return template;
        }
    }

    /**
     * Handle conditional template blocks
     * Format: {{#condition}}content{{/condition}} or {{#condition}}content{{else}}content{{/condition}}
     */
    private String handleConditionalBlocks(String template, Map<String, Object> dataMap) {
        String result = template;
        
        // Pattern for {{#condition}}content{{/condition}}
        String conditionPattern = "\\{\\{#([a-zA-Z0-9]+)\\}\\}(.*?)\\{\\{/\\1\\}\\}";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(conditionPattern, 
            java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher matcher = pattern.matcher(result);
        
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String condition = matcher.group(1);
            String content = matcher.group(2);
            
            // Check if condition is true (non-null, non-false, non-empty)
            Object conditionValue = dataMap.get(condition);
            boolean isTrue = conditionValue != null && 
                            !conditionValue.equals(false) && 
                            !conditionValue.equals("false") &&
                            !conditionValue.equals("");
            
            String replacement = isTrue ? content : "";
            matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }

    /**
     * Send HTML email using JavaMailSender
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException, java.io.UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        InternetAddress fromAddress = new InternetAddress(fromEmail, fromName, StandardCharsets.UTF_8.name());
        helper.setFrom(fromAddress);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);  // true = HTML content
        
        mailSender.send(message);
        log.info("HTML email sent successfully to: {}", to);
    }

    /**
     * Build subject line based on appointment status
     */
    private String buildStatusUpdateSubject(AppointmentEmailDTO emailData) {
        if (emailData.isApproved()) {
            return "Appointment Approved - Ready for Your Visit";
        } else if (emailData.isRejected()) {
            return "Appointment Request Declined - Please Try Another Doctor";
        } else if (emailData.isCompleted()) {
            return "Appointment Completed - Your Records are Available";
        }
        return "Appointment Status Update";
    }

    /**
     * Send Support Ticket Created Email
     */
    @Async
    public void sendSupportTicketCreatedEmail(SupportTicketEmailDTO emailData) {
        if (!notificationsEnabled) {
            log.info("Email skipped (notifications disabled): {}", emailData.getRecipientEmail());
            return;
        }
        try {
            Map<String, Object> dataMap = new ObjectMapper().convertValue(emailData, Map.class);
            String htmlContent = loadTemplate("support-ticket-created.html");
            htmlContent = populateTemplate(htmlContent, dataMap);
            sendHtmlEmail(emailData.getRecipientEmail(), 
                    "Support Ticket Created - ID #" + emailData.getTicketId(), 
                    htmlContent);
        } catch (Exception e) {
            log.warn("Failed to send support ticket created email to {}: {}", 
                    emailData.getRecipientEmail(), e.getMessage());
        }
    }

    /**
     * Send Support Ticket Status Update Email
     */
    @Async
    public void sendSupportTicketStatusUpdateEmail(SupportTicketEmailDTO emailData) {
        if (!notificationsEnabled) {
            log.info("Email skipped (notifications disabled): {}", emailData.getRecipientEmail());
            return;
        }
        try {
            Map<String, Object> dataMap = new ObjectMapper().convertValue(emailData, Map.class);
            String htmlContent = loadTemplate("support-ticket-status-update.html");
            htmlContent = populateTemplate(htmlContent, dataMap);
            sendHtmlEmail(emailData.getRecipientEmail(), 
                    "Support Ticket #" + emailData.getTicketId() + " - " + emailData.getStatus(), 
                    htmlContent);
        } catch (Exception e) {
            log.warn("Failed to send support ticket status update email to {}: {}", 
                    emailData.getRecipientEmail(), e.getMessage());
        }
    }

    /**
     * Send Support Message Reply Email
     */
    @Async
    public void sendSupportMessageReplyEmail(SupportMessageEmailDTO emailData) {
        if (!notificationsEnabled) {
            log.info("Email skipped (notifications disabled): {}", emailData.getRecipientEmail());
            return;
        }
        try {
            Map<String, Object> dataMap = new ObjectMapper().convertValue(emailData, Map.class);
            String htmlContent = loadTemplate("support-message-reply.html");
            htmlContent = populateTemplate(htmlContent, dataMap);
            sendHtmlEmail(emailData.getRecipientEmail(), 
                    "New Reply to Your Support Ticket #" + emailData.getTicketId(), 
                    htmlContent);
        } catch (Exception e) {
            log.warn("Failed to send support message reply email to {}: {}", 
                    emailData.getRecipientEmail(), e.getMessage());
        }
    }
}
