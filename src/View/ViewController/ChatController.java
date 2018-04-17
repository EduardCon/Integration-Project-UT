package View.ViewController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable{
    @FXML BorderPane borderPane;
    @FXML private TextArea messageBox;
    @FXML ListView chatPane;
    @FXML
    ImageView userImageView;

    public void sendButton() {
        String message = messageBox.getText();
        if(!messageBox.getText().isEmpty()) {
            chatPane.getItems().add(message);
            messageBox.clear();
        }
    }

    public synchronized void addToChat() {

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
