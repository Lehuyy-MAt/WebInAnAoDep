package com.example.webinanao.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendResetPasswordEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Đặt lại mật khẩu - WebInAnAo");
        message.setText("Xin chào,\n\n"
                + "Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.\n\n"
                + "Click vào link sau để đặt lại mật khẩu:\n"
                + resetLink + "\n\n"
                + "Link có hiệu lực trong 1 giờ.\n\n"
                + "Nếu bạn không yêu cầu, vui lòng bỏ qua email này.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ WebInAnAo");
        message.setFrom("haizxc123456@gmail.com");

        mailSender.send(message);
    }
}