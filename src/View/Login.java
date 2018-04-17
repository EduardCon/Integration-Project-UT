package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Login extends Application {
    Stage window;
    BorderPane layout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(this.getClass().getResource("/View/loginview.fxml"));
        root.getStylesheets().add(getClass().getResource("styles/Login.css").toExternalForm());
        primaryStage.setScene((new Scene(root)));
        primaryStage.show();
    }
}
