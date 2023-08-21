import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WelcomePage extends Application {
    private TextField usernameField;
    private TextField passwordField;

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Welcome to Bank App");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("styled-button");
        loginButton.setOnAction(event -> loginClicked());

        Button createAccountButton = new Button("Create Account");
        createAccountButton.getStyleClass().add("styled-button");
        createAccountButton.setOnAction(event -> openCreateAccountForm());

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMinWidth(loginButton.getMinWidth());

        passwordField = new TextField();
        passwordField.setPromptText("Password");
        passwordField.setMinWidth(loginButton.getMinWidth());

        HBox buttonRow = new HBox(10, loginButton, createAccountButton);
        buttonRow.setAlignment(Pos.CENTER);

        VBox inputFields = new VBox(10, usernameField, passwordField);
        inputFields.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(inputFields, buttonRow);

        Scene scene = new Scene(layout, 500, 500);
        scene.getStylesheets().add(WelcomePage.class.getResource("Style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loginClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authenticateUser(username, password)) {
            showAlert("Login successful. Welcome!");
        } else {
            showAlert("Login failed");
        }
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void openCreateAccountForm() {
        // Close the current stage
        Stage currentStage = (Stage) usernameField.getScene().getWindow();
        currentStage.close();

        // Open the "Create Account" form
        CreateAccount createAccountForm = new CreateAccount();
        Stage createAccountStage = new Stage();
        createAccountForm.start(createAccountStage);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
