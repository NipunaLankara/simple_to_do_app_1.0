package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CreateNewAccountFormController {
    public TextField txtUserName;
    public TextField txtEmail;
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;
    public Label lblUserId;
    public AnchorPane root;

    public void initialize () {
        setDisableCommon(true);
    }

    public void btnAddNewUserOnAction(ActionEvent actionEvent) {
        setDisableCommon(false);
        txtUserName.requestFocus();
        autoGenerateId();

    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (newPassword.equals(confirmPassword)) {
            setBorderClr("transparent");

            register();

        } else {
            setBorderClr("red");

            txtNewPassword.clear();
            txtConfirmPassword.clear();
            txtNewPassword.requestFocus();
        }

    }

    public void setDisableCommon (boolean bool) {
        txtUserName.setDisable(bool);
        txtEmail.setDisable(bool);
        txtNewPassword.setDisable(bool);
        txtConfirmPassword.setDisable(bool);
    }

    public void autoGenerateId() {
        Connection connection = DBConnection.getInstance().getConnection();

        try {

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select uid from user order by uid desc limit 1");

            boolean resultId = resultSet.next();

            if (resultId) {
                String userId = resultSet.getString(1);
                userId = userId.substring(1,userId.length());

                int intId = Integer.parseInt(userId);

                intId++;

                if (intId <10) {
                    lblUserId.setText("U00" +intId);

                } else if (intId <100) {
                    lblUserId.setText("U0" +intId);

                } else {
                    lblUserId.setText("U" +intId);
                }

            } else {
                lblUserId.setText("U001");
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void setBorderClr(String color) {
        txtNewPassword.setStyle("-fx-border-color: " + color);
        txtConfirmPassword.setStyle("-fx-border-color: " + color);
    }

    public void register()  {
        String userId = lblUserId.getText();
        String userName = txtUserName.getText();
        String email = txtEmail.getText();
        String password = txtConfirmPassword.getText();

        if (userName.trim().isEmpty()) {
            txtUserName.requestFocus();

        } else if (email.trim().isEmpty()) {
            txtEmail.requestFocus();

        } else if (txtNewPassword.getText().trim().isEmpty()) {
            txtNewPassword.requestFocus();

        } else if (password.trim().isEmpty()) {
            txtConfirmPassword.requestFocus();

        } else {
            Connection connection = DBConnection.getInstance().getConnection();

            try {

                PreparedStatement preparedStatement = connection.prepareStatement("insert into user values (?,?,?,?)");
                preparedStatement.setObject(1,userId);
                preparedStatement.setObject(2,userName);
                preparedStatement.setObject(3,email);
                preparedStatement.setObject(4,password);

                preparedStatement.executeUpdate();

                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("Login Form");
                primaryStage.centerOnScreen();

            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
