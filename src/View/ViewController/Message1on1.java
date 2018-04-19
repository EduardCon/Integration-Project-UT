package View.ViewController;

import ApplicationLayer.Client;
import Util.Utils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;


import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class Message1on1 implements Initializable, Observer {
    @FXML
    ImageView userImageView;
    @FXML
    private Button setPort;
    @FXML
    private TextField portText;
    @FXML
    private TextArea messageBox;
    @FXML
    private ListView chatPane;
    @FXML
    private Label usernameLabel;
    public LoginPanel loginPanel;
    Client client;
    private int portNumber;
    private String lastmessageinbox;
    private String username;

    public int getPortAction() {
        String port = portText.getText();
        this.setPortNumber(Integer.parseInt(port));
        return Integer.parseInt(port);
    }

    public void setClient(Client client) {
        this.client = client;
    }


    public Client getClient() {
        return this.client;
    }

    public void setPortNumber(int port) {
        this.portNumber = port;
    }

    public void sendButton() throws Exception {
        String message = messageBox.getText();
        this.portNumber = getPortAction();
        if (!messageBox.getText().isEmpty()) {
            //addToChat(client.getReceivedBuffer());
//            chatPane.getItems().add(client.getReceivedBuffer().values());
            lastmessageinbox = this.username + ": " + message;
            System.out.println("HAIDE " + loginPanel.getClient());
            loginPanel.getClient().sendToProceessingLayer(message, this.portNumber);
            messageBox.clear();
        }
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
                String lastIndex = "";
//                for(List<String> i : client.getReceivedBuffer().values()) {
//                    lastIndex = i.get(i.size()-1);
//                }
                bl6.setText(loginPanel.getClient().lastMessageTodDisplay);
                bl6.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
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

        if (loginPanel.getClient().getName().equals(usernameLabel.getText())) {
            Thread t2 = new Thread(yourMessages);
            t2.setDaemon(true);
            t2.start();
        } else {
            Thread t = new Thread(othersMessages);
            t.setDaemon(true);
            t.start();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        this.addToChat();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        FXMLLoader root = new FXMLLoader(getClass().getResource("/View/loginview.fxml"));
//        loginPanel = root.<LoginPanel>getController();
//        setClient(root.<LoginPanel>getController().getClient());
    }
}
