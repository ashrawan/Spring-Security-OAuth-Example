package com.demo.springcustomizedstarterexample.services.mail;

import com.demo.springcustomizedstarterexample.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailService extends AbstractDefaultEmailService {

    private String defaultSourceEmailAddress;
    private String officialCompanyName;
    private String officialCompanyDomain;

    private final JavaMailSender javaMailSender;
    private final FreeMarkerConfigurer freemarkerConfigurer;
    private final AppProperties appProperties;

    public EmailService(JavaMailSender javaMailSender,
                        FreeMarkerConfigurer freemarkerConfigurer,
                        AppProperties appProperties) {
        super(javaMailSender, freemarkerConfigurer, appProperties.getMail().getDefaultEmailAddress());
        this.javaMailSender = javaMailSender;
        this.freemarkerConfigurer = freemarkerConfigurer;
        this.appProperties = appProperties;
    }

    @PostConstruct
    protected void init() {
        Instant now = Instant.now();
        defaultSourceEmailAddress = appProperties.getMail().getDefaultEmailAddress();
        officialCompanyName = appProperties.getOfficialCompanyName();
        officialCompanyDomain = appProperties.getOfficialCompanyDomain();
    }

    public void sendVerificationEmail(String destinationEmail,
                                      String firstName,
                                      MultiValueMap<String, String> appendQueryParamsToPasswordResetLink) {
        log.info("Initiated: sendVerificationEmail - to {} ", destinationEmail);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            // Populate the template data for Email Verification
            Map<String, Object> templateData = new HashMap<>();
            templateData.put(MessageTemplateCodeUtil.TemplateKeys.verificationUserFirstName, firstName);
            templateData.putAll(MessageTemplateCodeUtil.templateDefaultValuesMap);
            String linkVerifyEmail = UriComponentsBuilder.fromUriString(officialCompanyDomain + "/verify")
                    .queryParams(appendQueryParamsToPasswordResetLink)
                    .queryParam("isProcessVerifyEmail", true)
                    .build().toUriString();
            templateData.put(MessageTemplateCodeUtil.TemplateKeys.linkEmailVerification, linkVerifyEmail);

            // Retrieving (verification-code mail) template file to set populated data
            String templatePath = MessageTemplateCodeUtil.TemplatesPath.EMAIL_VERIFICATION_MAIL.getTemplatePath();
            String templateContent = FreeMarkerTemplateUtils.processTemplateIntoString(
                    freemarkerConfigurer.getConfiguration().getTemplate(templatePath),
                    templateData);

            // Sending email
            helper.setTo(destinationEmail);
            helper.setSubject(MessageTemplateCodeUtil.subjectVerifyEmail + " for " + officialCompanyName);
            helper.setText(templateContent, true);
            javaMailSender.send(mimeMessage);

            log.info("Completed: sendVerificationEmail ");
        } catch (MessagingException e) {
            log.error("sendWelcomeEmail failed MessagingException {} ", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            log.error("sendWelcomeEmail failed Exception {} ", e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendPasswordResetEmail(String destinationEmail,
                                      String firstName,
                                      MultiValueMap<String, String> appendQueryParamsToVerificationLink) {
        log.info("Initiated: sendPasswordResetEmail - to {} ", destinationEmail);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            // Populate the template data for Password reset
            Map<String, Object> templateData = new HashMap<>();
            templateData.put(MessageTemplateCodeUtil.TemplateKeys.verificationUserFirstName, firstName);
            templateData.putAll(MessageTemplateCodeUtil.templateDefaultValuesMap);
            String linkPasswordReset = UriComponentsBuilder.fromUriString(officialCompanyDomain + "/verify")
                    .queryParams(appendQueryParamsToVerificationLink)
                    .queryParam("isProcessPasswordReset", true)
                    .build().toUriString();
            templateData.put(MessageTemplateCodeUtil.TemplateKeys.linkPasswordReset, linkPasswordReset);

            // Retrieving (password-reset mail) template file to set populated data
            String templatePath = MessageTemplateCodeUtil.TemplatesPath.RESET_PASSWORD_MAIL.getTemplatePath();
            String templateContent = FreeMarkerTemplateUtils.processTemplateIntoString(
                    freemarkerConfigurer.getConfiguration().getTemplate(templatePath),
                    templateData);

            // Sending email
            helper.setTo(destinationEmail);
            helper.setSubject(MessageTemplateCodeUtil.subjectResetPasswordEmail + " for " + officialCompanyName);
            helper.setText(templateContent, true);
            javaMailSender.send(mimeMessage);

            log.info("Completed: sendPasswordResetEmail ");
        } catch (MessagingException e) {
            log.error("sendPasswordResetEmail failed MessagingException {} ", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            log.error("sendPasswordResetEmail failed Exception {} ", e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendWelcomeEmail(String destinationEmail,
                                 String fullName) {
        log.info("Initiated: sendWelcomeEmail - toEmailAddress {} ", destinationEmail);
        String firstName = fullName.contains(" ") ? fullName.split(" ", 2)[0] : fullName;
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            // Populate the template data
            Map<String, Object> templateData = new HashMap<>();
            templateData.put(MessageTemplateCodeUtil.TemplateKeys.welcomedUserFirstName, firstName);
            templateData.putAll(MessageTemplateCodeUtil.templateDefaultValuesMap);
            templateData.put(MessageTemplateCodeUtil.TemplateKeys.setupItemList, MessageTemplateCodeUtil.welcomeTemplateSetupList);
            String visitOfficialSite = UriComponentsBuilder.fromUriString(officialCompanyDomain)
                    .queryParam("activateGuide", true)
                    .build().toUriString();
            templateData.put(MessageTemplateCodeUtil.TemplateKeys.visitOfficialSite, visitOfficialSite);

            // Retrieving (welcome mail) template file to set populated data
            String templatePath = MessageTemplateCodeUtil.TemplatesPath.WELCOME_MAIL.getTemplatePath();
            String templateContent = FreeMarkerTemplateUtils.processTemplateIntoString(
                    freemarkerConfigurer.getConfiguration().getTemplate(templatePath),
                    templateData);

            // Sending email
            helper.setTo(destinationEmail);
            helper.setSubject(MessageTemplateCodeUtil.subjectWelcomeEmail);
            helper.setText(templateContent, true);
            javaMailSender.send(mimeMessage);

            log.info("Completed: sendWelcomeEmail ");
        } catch (MessagingException e) {
            log.error("sendWelcomeEmail failed MessagingException {} ", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            log.error("sendWelcomeEmail failed Exception {} ", e.getMessage());
            e.printStackTrace();
        }
    }

}
