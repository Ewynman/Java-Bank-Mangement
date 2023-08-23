import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Random;

public class AuthHome extends Application {
    private int userId;
    private Label checkingBalanceLabel = new Label();
    private Label savingsBalanceLabel = new Label();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Authenticated Home");

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome to your account!");

        HBox accountInfoBox = new HBox(20);
        accountInfoBox.setAlignment(Pos.CENTER);

        Label checkingLabel = new Label("Checking Balance: ");
        Label savingsLabel = new Label("Savings Balance: ");

        accountInfoBox.getChildren().addAll(checkingLabel, checkingBalanceLabel, savingsLabel, savingsBalanceLabel);

        Button openAccountButton = new Button("Open Account");
        openAccountButton.setOnAction(event -> openAccountClicked());

        layout.getChildren().addAll(welcomeLabel, accountInfoBox, openAccountButton);

        Scene scene = new Scene(layout, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String generateRandomAccountNumber() {
        Random random = new Random();
        int accountNumber = 10000000 + random.nextInt(90000000);
        return String.valueOf(accountNumber);
    }

    private void openAccountClicked() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:data/bankMangement.db")) {
            String query = "SELECT CheckingAccount, SavingsAccount FROM Users WHERE UserID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                boolean hasCheckingAccount = resultSet.getBoolean("CheckingAccount");
                boolean hasSavingsAccount = resultSet.getBoolean("SavingsAccount");

                if (!hasCheckingAccount && !hasSavingsAccount) {
                    String accountNumber = generateRandomAccountNumber();
                    String insertQuery = "INSERT INTO CheckingAccounts (user_id, owner_name, account_number, balance) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setInt(1, userId);
                    insertStatement.setString(2, "Owner Name"); // Replace with appropriate owner name
                    insertStatement.setString(3, accountNumber);
                    insertStatement.setDouble(4, 0.0);
                    insertStatement.executeUpdate();

                    String updateQuery = "UPDATE Users SET CheckingAccount = ? WHERE UserID = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setBoolean(1, true);
                    updateStatement.setInt(2, userId);
                    updateStatement.executeUpdate();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Account Opened", "Checking account opened successfully.");

                    refreshAccountBalances(connection);
                } else {
                    showAlert(Alert.AlertType.WARNING, "Warning", "Account Already Exists", "You already have an account.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Account Opening Failed", "An error occurred while opening the account.");
        }
    }

    private void refreshAccountBalances(Connection connection) {
        try {
            String query = "SELECT CheckingBalance, SavingsBalance FROM Users WHERE UserID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double checkingBalance = resultSet.getDouble("CheckingBalance");
                double savingsBalance = resultSet.getDouble("SavingsBalance");
                checkingBalanceLabel.setText(String.format("$%.2f", checkingBalance));
                savingsBalanceLabel.setText(String.format("$%.2f", savingsBalance));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Account Balance Retrieval Failed", "An error occurred while fetching account balances.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
