package br.sesi.achadosperdidos.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Carrega configurações com prioridade: Variável de Ambiente > config.properties.
 * Uso: AppConfig.getDbUrl(), AppConfig.getMailUser(), etc.
 */
public final class AppConfig {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = AppConfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar config.properties", e);
        }
    }

    private AppConfig() {}

    // Env var tem prioridade; fallback para o arquivo .properties
    private static String resolve(String envName, String propKey) {
        String env = System.getenv(envName);
        return (env != null && !env.isBlank()) ? env : props.getProperty(propKey);
    }

    // --- Banco de dados ---

    public static String getDbUrl() {
        return resolve("DB_URL", "db.url");
    }

    public static String getDbUser() {
        return resolve("DB_USER", "db.user");
    }

    public static String getDbPassword() {
        return resolve("DB_PASS", "db.password");
    }

    // --- E-mail SMTP ---

    public static String getMailSmtpHost() {
        return props.getProperty("mail.smtp.host", "smtp.gmail.com");
    }

    public static int getMailSmtpPort() {
        return Integer.parseInt(props.getProperty("mail.smtp.port", "587"));
    }

    public static String getMailUser() {
        return resolve("MAIL_USER", "mail.smtp.user");
    }

    public static String getMailPassword() {
        return resolve("MAIL_PASS", "mail.smtp.password");
    }

    public static String getMailFromName() {
        return props.getProperty("mail.from.name", "Achados e Perdidos SESI");
    }
}
