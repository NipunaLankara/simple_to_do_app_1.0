package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoListFormController {
    public AnchorPane root;
    public Label lblWelcome;
    public Label lblUserId;
    public Pane subRoot;
    public TextField txtAddNewToDo;
    public ListView <ToDoTM>lstToDoList;
    public TextField txtSelectTodo;
    public Button btnDelete;
    public Button btnUpdate;


    public void initialize () {
        lblWelcome.setText("Hi "+LoginFormController.loginUserName + " Welcome to ToDO List");
        lblUserId.setText(LoginFormController.loginUserId);

        subRoot.setVisible(false);
        loadToD0();
        setDisableCommon(true);

        lstToDoList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {

                if (lstToDoList.getSelectionModel().getSelectedItem() == null) {
                    return;
                }
                setDisableCommon(false);
                subRoot.setVisible(false);

                txtSelectTodo.setText(lstToDoList.getSelectionModel().getSelectedItem().getDescription());

            }
        });


    }

    public void setDisableCommon(boolean bool) {
        txtSelectTodo.setDisable(bool);
        btnDelete.setDisable(bool);
        btnUpdate.setDisable(bool);

        txtSelectTodo.clear();
    }

    public void btnaddNewToDoOnAction(ActionEvent actionEvent) {
        subRoot.setVisible(true);
        txtAddNewToDo.requestFocus();
        setDisableCommon(true);

    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {
        String todoId = autoGenerateId();
        String description = txtAddNewToDo.getText();
        String userId = LoginFormController.loginUserId;

        Connection connection = DBConnection.getInstance().getConnection();
        try {

            PreparedStatement preparedStatement = connection.prepareStatement("Insert into todo values (?,?,?)");
            preparedStatement.setObject(1,todoId);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3,userId);

            preparedStatement.executeUpdate();

            txtAddNewToDo.clear();
            subRoot.setVisible(false);

            loadToD0();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public String autoGenerateId () {
        Connection connection = DBConnection.getInstance().getConnection();

        String id = "";

        try {

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select todo_id from todo order by todo_id desc limit 1");

            boolean resultId = resultSet.next();

            if (resultId) {
                String todoId = resultSet.getString(1);
                todoId = todoId.substring(1,todoId.length());

                int intId = Integer.parseInt(todoId);

                intId++;

                if (intId <10) {
                    id = ("T00" +intId);

                } else if (intId <100) {
                    id = ("T0" +intId);

                } else {
                    id = ("T" +intId);
                }

            } else {
                id = "T001";
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return id;

    }

    public void loadToD0() {
        ObservableList<ToDoTM> todos = lstToDoList.getItems();
        todos.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where uid = ?");
            preparedStatement.setObject(1,lblUserId.getText());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String todoId = resultSet.getString(1);
                String description = resultSet.getString(2);
                String userId = resultSet.getString(3);

                todos.add(new ToDoTM(todoId,description,userId));

            }
            lstToDoList.refresh();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String description = txtSelectTodo.getText();
        String todoID = lstToDoList.getSelectionModel().getSelectedItem().getTodoID();

        Connection connection = DBConnection.getInstance().getConnection();

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ? where todo_id = ?");
            preparedStatement.setObject(1,description);
            preparedStatement.setObject(2,todoID);

            preparedStatement.executeUpdate();
            loadToD0();
            setDisableCommon(true);


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"If you want to Delete this ?",ButtonType.YES,ButtonType.NO) ;
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)) {
            String todoID = lstToDoList.getSelectionModel().getSelectedItem().getTodoID();

            Connection connection = DBConnection.getInstance().getConnection();

            try {

                PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where todo_id = ?");
                preparedStatement.setObject(1,todoID);

                preparedStatement.executeUpdate();
                loadToD0();
                setDisableCommon(true);


            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }

    }

    public void btnLogOutOnAction(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Form");
        primaryStage.centerOnScreen();
    }
}
