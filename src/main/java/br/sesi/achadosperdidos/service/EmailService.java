package br.sesi.achadosperdidos.service;

import br.sesi.achadosperdidos.dao.ConnectionFactory;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class EmailService {

    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String fromName;

    public EmailService() {
        Properties cfg = new Properties();
        try (InputStream in = ConnectionFactory.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in != null) cfg.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar config.properties", e);
        }
        host = cfg.getProperty("mail.smtp.host", "smtp.gmail.com");
        port = Integer.parseInt(cfg.getProperty("mail.smtp.port", "587"));
        user = cfg.getProperty("mail.smtp.user", "");
        password = cfg.getProperty("mail.smtp.password", "");
        fromName = cfg.getProperty("mail.from.name", "Achados e Perdidos SESI");
    }

    public void enviarCodigoVerificacao(String toEmail, String nomeUsuario, String codigo) throws MessagingException, UnsupportedEncodingException {
        String subject = "Seu código de verificação — Achados e Perdidos SESI";
        String body =
            "<div style='font-family:Arial,sans-serif;max-width:520px;margin:0 auto;'>" +
            "<h2 style='color:#2d6a4f;'>Verificação de conta</h2>" +
            "<p>Olá, <strong>" + escapeHtml(nomeUsuario) + "</strong>!</p>" +
            "<p>Use o código abaixo para ativar sua conta. Ele expira em <strong>15 minutos</strong>.</p>" +
            "<div style='background:#f4f4f4;border-radius:8px;padding:24px;text-align:center;" +
                 "font-size:36px;letter-spacing:12px;font-weight:bold;color:#1b4332;'>" +
            escapeHtml(codigo) +
            "</div>" +
            "<p style='color:#888;font-size:13px;margin-top:20px;'>" +
            "Se você não criou esta conta, ignore este e-mail.</p>" +
            "</div>";
        enviar(toEmail, subject, body);
    }

    private void enviar(String to, String subject, String htmlBody) throws MessagingException, UnsupportedEncodingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(user, fromName, "UTF-8"));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject, "UTF-8");
        msg.setContent(htmlBody, "text/html; charset=UTF-8");
        Transport.send(msg);
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#x27;");
    }
}