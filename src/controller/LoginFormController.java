package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFormController {
    public TextField txtUserName;
    public PasswordField txtPassword;
    public AnchorPane root;

    public static String loginUserId;
    public static String loginUserName;

    public void btnLoginOnAction(ActionEvent actionEvent) {
        String name = txtUserName.getText();
        String password = txtPassword.getText();

        if (name.trim().isEmpty()) {
            txtUserName.requestFocus();
        } else if (password.trim().isEmpty()) {
            txtPassword.requestFocus();
        } else {
            Connection connection = DBConnection.getInstance().getConnection();

            try {

                PreparedStatement preparedStatement = connection.prepareStatement("select * from user where name = ? and password = ?");
                preparedStatement.setObject(1,name);
                preparedStatement.setObject(2,password);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    loginUserId = resultSet.getString(1);
                    loginUserName = resultSet.getString(2);

                    Parent parent =FXMLLoader.load(this.getClass().getResource("../view/ToDoListForm.fxml"));
                    Scene scene = new Scene(parent);

                    Stage primaryStage = (Stage) root.getScene().getWindow();
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("To Do List Form");
                    primaryStage.centerOnScreen();

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR,"Invalid User Name or Password!");
                    alert.showAndWait();

                    txtUserName.clear();
                    txtPassword.clear();
                    txtUserName.requestFocus();

                }

            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void lblCreateNewAccountOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/CreateNewAccountForm.fxml"));
        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Create New Account Form");
        primaryStage.centerOnScreen();
    }
}
