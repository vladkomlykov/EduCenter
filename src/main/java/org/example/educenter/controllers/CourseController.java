package org.example.educenter.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.educenter.data.DataBaseManager;

import java.sql.*;

public class CourseController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ListView<Object> courseList;
    private DataBaseManager dataBaseManager;

    public CourseController() {
        this.dataBaseManager = new DataBaseManager();
    }

    public void initialize() {
        String query = "SELECT course_id, course_name, duration, description, price, seats_available, instructor, image_url FROM Courses";
        ObservableList<Object> courses = FXCollections.observableArrayList();

        try (Connection connection = dataBaseManager.getConnection()) {
            if (connection == null) {
                System.err.println("Не удалось подключиться к базе данных");
                return;
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet set = preparedStatement.executeQuery()) {
                Image image;
                while (set.next()) {
                    String coursesInfo = String.format("Название: %s\nДлительность курса: %d дней\nОписание: %s\nЦена: %.2f\nДоступные места: %d\nПреподаватель: %s",
                            set.getString("course_name"),
                            set.getInt("duration"),
                            set.getString("description"),
                            set.getDouble("price"),
                            set.getInt("seats_available"),
                            set.getString("instructor")
                    );

                    image = new Image(set.getString("image_url"));
                    ImageView imageView = new ImageView();
                    imageView.setImage(image);
                    courses.add(coursesInfo);
                    courses.add(imageView);
                }

                System.out.println("Количество курсов: " + courses.size()); // Для отладки

            } catch (SQLException e) {
                System.err.println("Ошибка базы данных: " + e.getMessage());
            }

            courseList.setItems(courses);
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
        }
    }

    public void signUp(MouseEvent mouseEvent) {
        ObservableList selectedIndices = courseList.getSelectionModel().getSelectedIndices();
        for (Object o : selectedIndices) {
            String courseName = "";
            int selectedCourseId = 0;
            if (o.equals(0) || o.equals(1)) {
                courseName = "Веб-программирование";
                selectedCourseId = 16;
            } else if (o.equals(2) || o.equals(3)) {
                courseName = "Бэкенд-разработка";
                selectedCourseId = 17;
            } else if (o.equals(4) || o.equals(5)) {
                courseName = "Веб-дизайн";
                selectedCourseId = 18;
            } else if (o.equals(6) || o.equals(7)) {
                courseName = "Android-разработка";
                selectedCourseId = 19;
            } else if (o.equals(8) || o.equals(9)) {
                courseName = "Машинное обучение";
                selectedCourseId = 20;
            }
            showCoursePopup(courseName, selectedCourseId);
        }
    }

    private void showCoursePopup(String courseName, int selectedCourseId) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Информация о курсе");

        Label courseLabel = new Label("Вы выбрали курс: " + courseName);
        Button closeButton = new Button("Закрыть");
        Button enrollButton = new Button("Записаться");

        closeButton.getStyleClass().add("modal-buttons");
        enrollButton.getStyleClass().add("modal-buttons");

        closeButton.setOnAction((ActionEvent e) -> popupStage.close());
        enrollButton.setOnAction((ActionEvent e) -> showAuthStage(popupStage, selectedCourseId));

        VBox layout = new VBox(10);
        layout.getChildren().addAll(new Text(courseLabel.getText()), enrollButton, closeButton);
        layout.setStyle("-fx-padding: 10; -fx-alignment: center; -fx-background-color: #e2e2e2");

        Scene scene = new Scene(layout, 300, 150);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void showAuthStage(Stage parentStage, int selectedCourseId) {
        Stage authStage = new Stage();
        authStage.initModality(Modality.APPLICATION_MODAL);
        authStage.setTitle("Вход или регистрация");

        Label authLabel = new Label("Чтобы записаться на курс, войдите в систему или зарегистрируйтесь.");
        Button loginButton = new Button("Войти");
        Button registerButton = new Button("Зарегистрироваться");
        Button backButton = new Button("Назад");

        backButton.getStyleClass().add("modal-buttons");
        registerButton.getStyleClass().add("modal-buttons");
        loginButton.getStyleClass().add("modal-buttons");

        loginButton.setOnAction(e -> {
            showLogStage(parentStage, selectedCourseId);
            authStage.close();
        });

        registerButton.setOnAction(e -> {
            showRegStage(parentStage, selectedCourseId);
            authStage.close();
        });

        backButton.setOnAction(e -> authStage.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(authLabel, loginButton, registerButton, backButton);
        layout.setStyle("-fx-padding: 10; -fx-alignment: center;");

        Scene scene = new Scene(layout, 350, 200);
        authStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        authStage.initOwner(parentStage);
        authStage.showAndWait();
    }

    private void showLogStage(Stage parentStage, int selectedCourseId) {
        Stage logStage = new Stage();
        logStage.initModality(Modality.APPLICATION_MODAL);
        logStage.setTitle("Вход");

        TextField login = new TextField();
        PasswordField password = new PasswordField();
        Button signup = new Button("Записаться");
        Button backBtn = new Button("Назад");

        login.setPromptText("Логин");
        password.setPromptText("Пароль");
        signup.getStyleClass().add("modal-buttons");
        backBtn.getStyleClass().add("modal-buttons");

        signup.setOnAction(e -> {
            String queryLogin = "SELECT user_id, username, password FROM Users WHERE username = ?";
            String queryEnrollments = "INSERT INTO Enrollments(user_id, course_id, enrollment_date, payment_status) VALUES(?, ?, ?, ?)";

            try(Connection connection = dataBaseManager.getConnection()){
                if (connection == null){
                    System.err.println("Не удалось подключиться к базе данных");
                    return;
                }

                try(PreparedStatement statement = connection.prepareStatement(queryLogin)){
                    statement.setString(1, login.getText());

                    try(ResultSet resultSet = statement.executeQuery()){
                        if (resultSet.next()){
                            int userId = resultSet.getInt("user_id");
                            String storedPassword = resultSet.getString("password");

                            if (!storedPassword.equals(password.getText())){
                                System.err.println("Неверный пароль");
                                return;
                            }
                            try(PreparedStatement enrollStatement = connection.prepareStatement(queryEnrollments)){
                                enrollStatement.setInt(1,userId);
                                enrollStatement.setInt(2, selectedCourseId);
                                enrollStatement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                                enrollStatement.setString(4, "Не оплачен");

                                enrollStatement.executeUpdate();
                                System.out.println("Пользователь успешно записан на курс");

                                logStage.close();
                                parentStage.close();
                            }
                        } else {
                            System.err.println("Пользователь не найден");
                        }
                    }
                }
            } catch (SQLException exception) {
                System.err.println("Ошибка базы данных: " + exception.getMessage());
            }
        });

        backBtn.setOnAction(e -> logStage.close());

        VBox layout = new VBox(10, login, password, signup, backBtn);
        layout.setStyle("-fx-padding: 10; -fx-alignment: center;");

        Scene scene = new Scene(layout, 350, 200);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        logStage.setScene(scene);
        logStage.showAndWait();
    }

    private void showRegStage(Stage parentStage, int selectedCourseId) {
        Stage regStage = new Stage();
        regStage.initModality(Modality.APPLICATION_MODAL);
        regStage.setTitle("Регистрация");

        String[] genders = {"Мужской", "Женский"};

        TextField fullName = new TextField();
        DatePicker birth = new DatePicker();
        ChoiceBox gender = new ChoiceBox(FXCollections.observableArrayList(genders));
        TextField login = new TextField();
        PasswordField password = new PasswordField();
        Button regBtn = new Button("Зарегистрироваться");
        Button backBtn = new Button("Назад");

        fullName.setPromptText("ФИО");
        gender.setValue("Мужской");
        login.setPromptText("Логин");
        password.setPromptText("Пароль");
        regBtn.getStyleClass().add("modal-buttons");
        backBtn.getStyleClass().add("modal-buttons");

        regBtn.setOnAction(e -> {

            if (fullName.getText().isEmpty() || birth.getValue() == null || login.getText().isEmpty() || password.getText().isEmpty()) {
                System.err.println("Все поля должны быть заполнены");
                System.out.println(fullName.getText() + "\n" + birth.getValue() + "\n" + login.getText() + "\n" + password.getText());
                return;
            }
            String userQuery = "INSERT INTO Users(full_name, date_of_birth, gender, username, password) VALUES (?, ?, ?, ?, ?)";
            String enrollmentQuery = "INSERT INTO Enrollments(user_id, course_id, enrollment_date, payment_status) VALUES (?, ?, ?, ?)";

            try (Connection connection = dataBaseManager.getConnection()) {
                if (connection == null) {
                    System.err.println("Не удалось подключиться к базе данных");
                    return;
                }

                connection.setAutoCommit(false);

                try (PreparedStatement registerStatement = connection.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS)) {
                    registerStatement.setString(1, fullName.getText());
                    registerStatement.setDate(2, java.sql.Date.valueOf(birth.getValue()));
                    registerStatement.setString(3, gender.getValue().toString());
                    registerStatement.setString(4, login.getText());
                    registerStatement.setString(5, password.getText());

                    int affectedRows = registerStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Ошибка при регистрации пользователя, строка не добавлена.");
                    }

                    ResultSet generatedKeys = registerStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);

                        try (PreparedStatement enrollmentStatement = connection.prepareStatement(enrollmentQuery)) {
                            enrollmentStatement.setInt(1, userId);
                            enrollmentStatement.setInt(2, selectedCourseId);
                            enrollmentStatement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                            enrollmentStatement.setString(4, "Не оплачено");

                            enrollmentStatement.executeUpdate();
                        }

                        connection.commit();
                        System.out.println("Пользователь успешно зарегистрирован и записан на курс!");
                        regStage.close();
                        parentStage.close();
                    } else {
                        throw new SQLException("Не удалось получить user_id после регистрации.");
                    }
                } catch (SQLException ex) {
                    connection.rollback();
                    throw ex;
                }
            } catch (SQLException exception) {
                System.err.println("Ошибка базы данных: " + exception.getMessage());
            }
        });
        backBtn.setOnAction(e -> regStage.close());

        VBox layout = new VBox(10, fullName, birth, gender, login, password, regBtn, backBtn);
        layout.setStyle("-fx-padding: 10; -fx-alignment: center;");

        Scene scene = new Scene(layout, 350, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        regStage.setScene(scene);
        regStage.showAndWait();
    }
}
