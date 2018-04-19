package View.ViewController;

import ApplicationLayer.Client;
import Util.Utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ChatController implements Initializable, Observer {
    @FXML BorderPane borderPane;
    @FXML private TextArea messageBox;
    @FXML ListView chatPane;
    @FXML ImageView userImageView;
    @FXML private Label usernameLabel;
    @FXML private Label onlineCountLabel;
    int portNumber;
    Stage stage=new Stage();
    private String username;
    private Client client;
    public LoginPanel login;
    private String lastmessageinbox;

    public void sendButton() throws Exception {
        String message = messageBox.getText();
        if(!messageBox.getText().isEmpty()) {
            //addToChat(client.getReceivedBuffer());
//            chatPane.getItems().add(client.getReceivedBuffer().values());
            lastmessageinbox = this.username + ": " + message;
            client.sendToProceessingLayer(message, Utils.multiCastGroupPort);
            messageBox.clear();
        }
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getPortNumber() {
        return this.portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public void newClientView() throws IOException{
        Parent fxmlLoader = FXMLLoader.load((getClass().getResource("/View/OneOnOne.fxml")));
        stage.setScene(new Scene(fxmlLoader));
        stage.show();

    }

    public void addToChat() {
        Task<HBox> othersMessages = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
//                Image image = new Image(getClass().getClassLoader().getResource("images/" + msg.getPicture() + ".png").toString());
//                ImageView profileImage = new ImageView(image);
//                profileImage.setFitHeight(32);
//                profileImage.setFitWidth(32);
                BubbledLabel bl6 = new BubbledLabel();
                String lastIndex= "";
//                for(List<String> i : client.getReceivedBuffer().values()) {
//                    lastIndex = i.get(i.size()-1);
//                }
                bl6.setText(client.lastMessageTodDisplay);
                bl6.setBackground(new Background(new BackgroundFill(Color.WHITE,null, null)));
                HBox x = new HBox();
                bl6.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
                x.getChildren().add(bl6);
//                setOnlineLabel(Integer.toString(msg.getOnlineCount()));
                return x;
            }
        };
        System.out.println(othersMessages.getValue());
        othersMessages.setOnSucceeded(event -> {
            chatPane.getItems().add(othersMessages.getValue());
        });

        Task<HBox> yourMessages = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                Image image = userImageView.getImage();
                ImageView profileImage = new ImageView(image);
                profileImage.setFitHeight(32);
                profileImage.setFitWidth(32);

                BubbledLabel bl6 = new BubbledLabel();
                bl6.setText(lastmessageinbox);


                bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN,
                        null, null)));
                HBox x = new HBox();
                x.setMaxWidth(chatPane.getWidth() - 20);
                x.setAlignment(Pos.TOP_RIGHT);
                bl6.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);
                x.getChildren().addAll(bl6, profileImage);

                //setOnlineLabel(Integer.toString(msg.getOnlineCount()));
                return x;
            }
        };
        yourMessages.setOnSucceeded(event -> chatPane.getItems().add(yourMessages.getValue()));

        if (client.getName().equals(usernameLabel.getText())) {
            Thread t2 = new Thread(yourMessages);
            t2.setDaemon(true);
            t2.start();
        } else {
            Thread t = new Thread(othersMessages);
            t.setDaemon(true);
            t.start();
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

    public void setOnlineLabel(String usercount) {
        Platform.runLater(() -> onlineCountLabel.setText(usercount));
    }

public void setImageLabel() {
    this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("images/default.png").toString()));
}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
    this.username=username;
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("sunt aici");
        if(o == this.client) {
            addToChat();
        } else if (o == this.client.getRoutingTable()) {
            setOnlineLabel(Integer.toString(this.client.getRoutingTable().getOnlineUsers()));
        }
    }
}
