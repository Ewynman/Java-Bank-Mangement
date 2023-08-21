import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateAccount extends Application {
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField emailField;
    private TextField usernameField;
    private TextField passwordField;
    private RadioButton checkingRadioButton;
    private RadioButton savingsRadioButton;

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Create Account");

        ToggleGroup accountTypeToggleGroup = new ToggleGroup();

        checkingRadioButton = new RadioButton("Checking");
        checkingRadioButton.setToggleGroup(accountTypeToggleGroup);
        checkingRadioButton.setSelected(true);

        savingsRadioButton = new RadioButton("Savings");
        savingsRadioButton.setToggleGroup(accountTypeToggleGroup);

        firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        emailField = new TextField();
        emailField.setPromptText("Email");

        usernameField = new TextField();
        usernameField.setPromptText("Username");

        passwordField = new TextField();
        passwordField.setPromptText("Password");

        Button createButton = new Button("Create Account");
        createButton.getStyleClass().add("styled-button");
        createButton.setOnAction(event -> createAccountClicked());

        HBox accountTypeRow = new HBox(10, checkingRadioButton, savingsRadioButton);
        accountTypeRow.setAlignment(Pos.CENTER);

        VBox inputFields = new VBox(10, firstNameField, lastNameField, emailField, usernameField, passwordField, accountTypeRow);
        inputFields.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(inputFields, createButton);

        Scene scene = new Scene(layout, 500, 500);
        scene.getStylesheets().add(CreateAccount.class.getResource("Style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createAccountClicked() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean checkingAccount = checkingRadioButton.isSelected();
        boolean savingsAccount = savingsRadioButton.isSelected();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO Users (FirstName, LastName, Email, Username, Password, CheckingAccount, SavingsAccount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, username);
            preparedStatement.setString(5, password);
            preparedStatement.setBoolean(6, checkingAccount);
            preparedStatement.setBoolean(7, savingsAccount);

            preparedStatement.executeUpdate();
            System.out.println("Account created successfully");

            showSuccessMessageAndReturnToHomePage();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Account creation failed");
        }
    }

    private void showSuccessMessageAndReturnToHomePage() {
        // Display a success message
        System.out.println("Account created. Please login.");

        // Open the "WelcomePage" form
        WelcomePage welcomePage = new WelcomePage();
        Stage welcomePageStage = new Stage();
        welcomePage.start(welcomePageStage);
        welcomePageStage.show();
        closeCurrentStage();
    }

    private void closeCurrentStage() {
        // Close the current "Create Account" stage
        Stage currentStage = (Stage) firstNameField.getScene().getWindow();
        currentStage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
