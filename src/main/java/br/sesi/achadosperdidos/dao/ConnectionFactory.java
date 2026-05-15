package br.sesi.achadosperdidos.dao;

import br.sesi.achadosperdidos.util.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL      = AppConfig.getDbUrl();
    private static final String USER     = AppConfig.getDbUser();
    private static final String PASSWORD = AppConfig.getDbPassword();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL não encontrado no classpath", e);
        }
    }

    private ConnectionFactory() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
