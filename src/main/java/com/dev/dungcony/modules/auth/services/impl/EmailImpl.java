package com.dev.dungcony.modules.auth.services.impl;

import com.dev.dungcony.modules.auth.config.MailProperties;
import com.dev.dungcony.modules.auth.exceptions.SendEmailException;
import com.dev.dungcony.modules.auth.services.interfaces.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailImpl implements EmailService {

    private static final RestClient SENDGRID = RestClient.builder()
            .baseUrl("https://api.sendgrid.com")
            .build();

    private static final Pattern FROM_NAME_EMAIL = Pattern.compile("^(.+?)\\s*<([^>]+)>\\s*$");

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final ObjectMapper objectMapper;

    @Value("${spring.mail.host:unknown}")
    private String mailHost;

    @Value("${spring.mail.port:587}")
    private int mailPort;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    /** Khi có (Railway: SENDGRID_API_KEY), gửi qua HTTPS :443 — tránh chặn outbound SMTP 587. */
    @Value("${app.email.sendgrid-api-key:}")
    private String sendgridApiKey;

    @Override
    public void sendNewPassword(String email, String newPassword) {
        send(email, "Mật khẩu mới của bạn", buildResetPassContent(newPassword));
    }

    @Override
    public void sendOtpChangeEmail(String email, String otp) {
        send(email, "Mã OTP xác nhận thay đổi email", buildEmailChangeContent(otp));
    }

    @Override
    public void sendOtpRegis(String email, String otp) {
        send(email, "Mã OTP đăng ký tài khoản", buildOtpContent(otp));
    }

    private void send(String email, String subject, String body) {
        if (sendgridApiKey != null && !sendgridApiKey.isBlank()) {
            try {
                log.info("Bắt đầu gửi email (SendGrid API HTTPS) tới: {} | from: {}", email, mailProperties.getFrom());
                sendViaSendGridApi(email, subject, body);
                log.info("Đã gửi email tới: {}", email);
                return;
            } catch (Exception e) {
                log.error("Lỗi SendGrid API tới: {} | {}", email, buildCauseChainForLog(e), e);
                throw new SendEmailException();
            }
        }

        try {
            log.info("Bắt đầu gửi email (SMTP) — tới: {} | SMTP {}:{} | from: {} | user: {} (mật khẩu không ghi log)",
                    email, mailHost, mailPort, mailProperties.getFrom(), maskUsername(mailUsername));
            mailSender.send(getMail(
                    email,
                    subject,
                    body));
            log.info("Đã gửi email tới: {}", email);
        } catch (Exception e) {
            String chain = buildCauseChainForLog(e);
            String hint = diagnoseSmtpError(chain);
            log.error(
                    "Lỗi gửi email tới: {} | phân loại: {} | nội dung: {} | smtp: {}:{} | user: {}",
                    email, hint, chain, mailHost, mailPort, maskUsername(mailUsername), e);
            throw new SendEmailException();
        }
    }

    private void sendViaSendGridApi(String toEmail, String subject, String textBody) throws JsonProcessingException {
        ParsedFrom from = parseFromHeader(mailProperties.getFrom());
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("personalizations", List.of(Map.of("to", List.of(Map.of("email", toEmail)))));
        Map<String, String> fromMap = new LinkedHashMap<>();
        fromMap.put("email", from.email());
        if (from.name() != null && !from.name().isBlank()) {
            fromMap.put("name", from.name());
        }
        payload.put("from", fromMap);
        payload.put("subject", subject);
        payload.put("content", List.of(Map.of("type", "text/plain", "value", textBody)));

        String json = objectMapper.writeValueAsString(payload);

        try {
            SENDGRID.post()
                    .uri("/v3/mail/send")
                    .header("Authorization", "Bearer " + sendgridApiKey.trim())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            log.error("SendGrid HTTP {} — body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        }
    }

    private record ParsedFrom(String email, String name) {}

    private static ParsedFrom parseFromHeader(String raw) {
        if (raw == null || raw.isBlank()) {
            return new ParsedFrom("", "");
        }
        String s = raw.trim();
        Matcher m = FROM_NAME_EMAIL.matcher(s);
        if (m.matches()) {
            return new ParsedFrom(m.group(2).trim(), m.group(1).trim());
        }
        return new ParsedFrom(s, "");
    }

    /** Che bớt username SMTP để log an toàn (vẫn nhận ra có set env hay chưa). */
    private static String maskUsername(String u) {
        if (u == null || u.isBlank()) {
            return "(rỗng — kiểm tra MAIL_USERNAME / spring.mail.username trên Railway)";
        }
        int at = u.indexOf('@');
        if (at <= 1) {
            return "***";
        }
        return u.charAt(0) + "***" + u.substring(at);
    }

    private static String buildCauseChainForLog(Throwable e) {
        StringBuilder sb = new StringBuilder();
        Throwable t = e;
        int d = 0;
        while (t != null && d < 10) {
            sb.append('[').append(t.getClass().getName()).append("] ");
            if (t.getMessage() != null) {
                sb.append(t.getMessage().replace('\n', ' ').trim());
            }
            sb.append(" | ");
            t = t.getCause();
            d++;
        }
        return sb.toString();
    }

    private static String diagnoseSmtpError(String chain) {
        String s = chain.toLowerCase();
        if (s.contains("authenticat")
                || s.contains("535")
                || s.contains("534")
                || s.contains("530 5.5.1")
                || s.contains("username and password not accepted")
                || s.contains("application-specific password")
                || s.contains("less secure app")) {
            return "SMTP_ĐĂNG_NHẬP_HOẶC_CHÍNH_SÁCH (mật khẩu API key / 2FA / nhà cung cấp chặn)";
        }
        if (s.contains("could not connect")
                || s.contains("couldn't connect to host")
                || s.contains("mailconnectexception")
                || s.contains("mail server connection failed")
                || s.contains("connection refused")
                || s.contains("connection timed out")
                || s.contains("operation timed out")
                || s.contains("unknown host")
                || s.contains("network is unreachable")
                || s.contains("i/o error")) {
            return "MẠNG_KHÔNG_TỚI_SMTP (timeout — PaaS như Railway thường chặn outbound 25/465/587; đặt SENDGRID_API_KEY để gửi qua API HTTPS)";
        }
        if (s.contains("starttls")
                || s.contains("ssl")
                || s.contains("handshake")
                || s.contains("certificate")
                || s.contains("pkix")) {
            return "TLS_SSL (cấu hình starttls/ssl)";
        }
        return "KHÁC (xem nội dung exception phía dưới)";
    }

    private String buildOtpContent(String otp) {
        return """
                Xin chào,

                Mã OTP của bạn là: %s

                Mã này sẽ hết hạn sau 5 phút.

                Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.

                Trân trọng,
                DungCony Team
                """.formatted(otp);
    }

    private String buildResetPassContent(String newPas) {
        return """
                Xin chào,

                Pass mới của bạn là: %s

                Hãy đổi mật khẩu khi nhận được tin nhắn này

                Trân trọng,
                DungCony Team
                """.formatted(newPas);
    }

    private String buildEmailChangeContent(String otp) {
        return """
                Xin chào,

                Nếu đây là yêu cầu của bạn, mã OTP để xác nhận thay đổi email là: %s

                Mã này sẽ hết hạn sau 5 phút.

                Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.

                Trân trọng,
                DungCony Team
                """.formatted(otp);
    }

    private SimpleMailMessage getMail(String email, String subject, String text) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(mailProperties.getFrom());
        mail.setTo(email);
        mail.setSubject(subject);
        mail.setText(text);

        return mail;
    }

}
