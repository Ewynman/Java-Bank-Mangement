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
import java.util.Random;

public class CreateAccount extends Application {
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField emailField;
    private TextField usernameField;
    private TextField passwordField;
    private RadioButton checkingRadioButton;
    private RadioButton savingsRadioButton;

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
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
        createButton.setOnAction(event -> createAccountClicked());

        Button backToLoginButton = new Button("Back to Login");
        backToLoginButton.setOnAction(event -> backToLoginPage());

        HBox accountTypeRow = new HBox(10, checkingRadioButton, savingsRadioButton);
        accountTypeRow.setAlignment(Pos.CENTER);

        VBox inputFields = new VBox(10, firstNameField, lastNameField, emailField, usernameField, passwordField, accountTypeRow, createButton, backToLoginButton);
        inputFields.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(inputFields);

        Scene scene = new Scene(layout, 500, 500);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String generateRandomAccountNumber() {
        Random random = new Random();
        int accountNumber = 10000000 + random.nextInt(90000000);
        return String.valueOf(accountNumber);
    }

    private void createAccountClicked() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean checkingAccount = checkingRadioButton.isSelected();
        boolean savingsAccount = savingsRadioButton.isSelected();

        if (!isValidEmail(email)) {
            System.out.println("Invalid email address");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO Users (FirstName, LastName, Email, Username, Password) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, username);
            preparedStatement.setString(5, password);

            preparedStatement.executeUpdate();

            int userId;
            try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            System.out.println("Account created successfully");

            if (checkingAccount) {
                createCheckingAccount(connection, userId);
            }

            if (savingsAccount) {
                createSavingsAccount(connection, userId);
            }

            showSuccessMessageAndReturnToHomePage();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Account creation failed");
        }
    }

    private void createCheckingAccount(Connection connection, int userId) {
        String accountNumber = generateRandomAccountNumber();
        String query = "INSERT INTO CheckingAccounts (user_id, owner_name, account_number, balance) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, firstNameField.getText() + " " + lastNameField.getText());
            preparedStatement.setString(3, accountNumber);
            preparedStatement.setDouble(4, 0);
            preparedStatement.executeUpdate();
            System.out.println("Checking account created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Checking account creation failed");
        }
    }

    private void createSavingsAccount(Connection connection, int userId) {
        String accountNumber = generateRandomAccountNumber();
        String query = "INSERT INTO SavingsAccounts (user_id, owner_name, account_number, balance) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, firstNameField.getText() + " " + lastNameField.getText());
            preparedStatement.setString(3, accountNumber);
            preparedStatement.setDouble(4, 0);
            preparedStatement.executeUpdate();
            System.out.println("Savings account created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Savings account creation failed");
        }
    }

    private boolean isValidEmail(String email) {
        return email.contains("@");
    }

    private void showSuccessMessageAndReturnToHomePage() {
        System.out.println("Account created. Please login.");
        backToLoginPage();
    }

    private void backToLoginPage() {
        WelcomePage welcomePage = new WelcomePage();
        Stage welcomePageStage = new Stage();
        welcomePage.start(welcomePageStage);
        welcomePageStage.show();
        primaryStage.close();
    }
}
