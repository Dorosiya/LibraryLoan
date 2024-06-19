//package portfolio.LibraryLoan.service;
//
//import jakarta.mail.Message;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.internet.MimeMessage;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.io.UnsupportedEncodingException;
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class SendEmailService {
//
//    private final JavaMailSender mailSender;
//
//    @Value("${spring.mail.username}")
//    private String sender;
//    private static final String SENDER_NAME = "관리자";
//
//    @Async("taskExecutor")
//    public void sendEmail(String sendTo, String subject, String text) {
//        try {
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            mimeMessage.setFrom(new InternetAddress(sender, SENDER_NAME));
//            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(sendTo));
//            mimeMessage.setSubject(subject);
//            mimeMessage.setText(text);
//            mailSender.send(mimeMessage);
//            log.info("send Email");
//        } catch (MessagingException e) {
//            log.error("메일 전송 실패 ", e);
//        } catch (UnsupportedEncodingException e) {
//            log.error("인코딩 실패", e);
//        }
//    }
//
//}
