package org.example.educenter.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
@Setter
@AllArgsConstructor
public class DataBaseManager {

    private String url;
    private String user;
    private String password;

    public DataBaseManager() {
        this.url = "jdbc:postgresql://localhost:5432/EduCenter";
        this.user = "postgres";
        this.password = "123";
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            System.err.println("Драйвер не найден: " + e.getMessage());
            throw new SQLException("Драйвер не найден", e);
        } catch (SQLException e) {
            System.err.println("Ошибка при подключении к базе данных: " + e.getMessage());
            throw e;
        }
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e){
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }
}
