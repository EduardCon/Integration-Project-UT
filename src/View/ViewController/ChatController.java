package View.ViewController;

import ApplicationLayer.Client;
import Util.Utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ChatController implements Initializable{
    @FXML BorderPane borderPane;
    @FXML private TextArea messageBox;
    @FXML ListView chatPane;
    @FXML ImageView userImageView;
    @FXML private Label onlineCountLabel;
    int portNumber;
    Stage stage;

    public void sendButton() throws Exception {
        Client client = new Client(messageBox.getText(), getPortNumber());
        client.connect();
        String message = messageBox.getText();
        System.out.println("PORT NUMBER " + getPortNumber());
        System.out.println("MESSAGE " + message);
        if(!messageBox.getText().isEmpty()) {
//            addToChat(client.getReceivedBuffer());
            System.out.println(!client.getReceivedBuffer().isEmpty());
            chatPane.getItems().add(client.getReceivedBuffer().values());
            System.out.println("PORT NUMBER " + getPortNumber());
            client.sendToProceessingLayer(message, 4464);
            messageBox.clear();
        }
    }

    public int getPortNumber() {
        return this.portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public synchronized void addToChat(Map<Integer, List<String>> receivedBuffer) {
        Task<HBox> othersMessages = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
//                Image image = new Image(getClass().getClassLoader().getResource("images/" + msg.getPicture() + ".png").toString());
//                ImageView profileImage = new ImageView(image);
//                profileImage.setFitHeight(32);
//                profileImage.setFitWidth(32);
                BubbledLabel bl6 = new BubbledLabel();
//                bl6.setText(msg.getName() + ": " + msg.getMsg());
                bl6.setBackground(new Background(new BackgroundFill(Color.WHITE,null, null)));
                HBox x = new HBox();
                bl6.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
//                x.getChildren().addAll(profileImage, bl6);
//                setOnlineLabel(Integer.toString(msg.getOnlineCount()));
                return x;
            }
        };

        othersMessages.setOnSucceeded(event -> {
            chatPane.getItems().add(othersMessages.getValue());
        });
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

    public void setOnlineLabel(String usercount) {
        Platform.runLater(() -> onlineCountLabel.setText(usercount));
    }

public void setImageLabel() {
    this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("images/default.png").toString()));
}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            //setImageLabel();
    }
    }
