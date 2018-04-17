package View.ViewController;

import ApplicationLayer.Client;
import Util.Utils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable{
    @FXML BorderPane borderPane;
    @FXML private TextArea messageBox;
    @FXML ListView chatPane;
    @FXML ImageView userImageView;
    int portNumber;
    Stage stage;

    public void sendButton() throws Exception {
        Client client = new Client(messageBox.getText(), getPortNumber());
        client.connect();
        String message = messageBox.getText();
        System.out.println("PORT NUMBER " + getPortNumber());
        System.out.println("MESSAGE " + message);
        if(!messageBox.getText().isEmpty()) {
            chatPane.getItems().add(message);
            System.out.println("PORT NUMBER " + getPortNumber());
            client.sendToProceessingLayer(message, Utils.multiCastGroupPort);
            messageBox.clear();
        }
    }

    public int getPortNumber() {
        return this.portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public synchronized void addToChat() {
        Task<HBox> otherMessages = new Task<HBox>() {
            @Override
            protected HBox call() throws Exception {
                return null;
            }
        }
    }

    public void setImageLabel(String selectedPicture) {
        switch (selectedPicture) {
            case "EduardC":
                this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("View/images/eduardC.jpg").toString()));
                break;
            case "EduardM":
                this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("View/images/EduardM.jpg").toString()));
                break;
            case "Default":
                this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("View/images/default.png").toString()));
                break;
        }
    }

public void setImageLabel() {
    this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("images/default.png").toString()));
}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            //setImageLabel();
    }
    }
