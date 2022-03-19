package com.demo.springcustomizedstarterexample.services.mail;

import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public abstract class AbstractDefaultEmailService {

    private final JavaMailSender javaMailSender;
    private final FreeMarkerConfigurer freemarkerConfigurer;
    private final String defaultSourceEmailAddress;

    protected AbstractDefaultEmailService(JavaMailSender javaMailSender,
                                          FreeMarkerConfigurer freemarkerConfigurer,
                                          String defaultSourceEmailAddress) {
        this.javaMailSender = javaMailSender;
        this.freemarkerConfigurer = freemarkerConfigurer;
        this.defaultSourceEmailAddress = defaultSourceEmailAddress;
    }

    public void sendSimpleMessage(String destinationEmail,
                                  String subject,
                                  String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(defaultSourceEmailAddress);
            message.setTo(destinationEmail);
            message.setSubject(subject);
            message.setText(text);

            javaMailSender.send(message);
        } catch (MailException e) {
            log.error("sendSimpleMessage failed MessagingException {} ", e.getMessage());
        }
    }

    public void sendSimpleMessageUsingTemplate(String destinationEmail,
                                               String subject,
                                               String templateText,
                                               String... templateModel) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setText(templateText);

        // Sample Example
        // templateText = "Hello \n%s\n, \n This is the default fallback test email template for you. \n Send By: %s \n";
        // String[] templateModel = { "TestUser", "Spring-boot-app" };

        String text = String.format(simpleMailMessage.getText(), templateModel);
        sendSimpleMessage(destinationEmail, subject, text);
    }

    public void sendMessageWithAttachment(String destinationEmail,
                                          String subject,
                                          String text,
                                          String pathToAttachment,
                                          String attachmentFilename) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(defaultSourceEmailAddress);
            helper.setTo(destinationEmail);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment(attachmentFilename, file);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("sendMessageWithAttachment failed MessagingException {} ", e.getMessage());
        }
    }

    public void sendMessageUsingFreemarkerTemplate(String destinationEmail,
                                                   String subject,
                                                   Map<String, Object> templateModel,
                                                   MessageTemplateCodeUtil.TemplatesPath templatesPath) {

        log.info("Initiated: sendMessageUsingFreemarkerTemplate - template: {} , toEmailAddress ", templatesPath.getTemplatePath(), destinationEmail);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {

            Template freemarkerTemplate = freemarkerConfigurer.getConfiguration().getTemplate(templatesPath.getTemplatePath());
            String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, templateModel);

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setTo(destinationEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("sendMessageUsingFreemarkerTemplate failed MessagingException {} ", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            log.error("sendMessageUsingFreemarkerTemplate failed Exception {} ", e.getMessage());
            e.printStackTrace();
        }
    }

}
