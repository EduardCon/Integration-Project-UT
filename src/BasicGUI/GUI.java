package BasicGUI;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class GUI {
    GUI GUI ;
    JFrame firstFrame;
    JFrame newFrame = new JFrame("Fantastic Mr. Fox");
    JTextField msgBox;
    JTextField chooseUsername;
    JButton sendMsg;
    JTextArea chatBox;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        GUI GUI = new GUI();
        GUI.firstDisplay();

    }

    public void firstDisplay() {
        newFrame.setVisible(false);
        firstFrame = new JFrame("Grand Budapest Hotel");
        chooseUsername = new JTextField(15);
        JLabel usernameLabel = new JLabel("Alege fmm ce stai pe ganduri");
        JButton enter = new JButton("Ho ca nimeni nu vrea sa vorbeasca cu tine");
        JPanel firstPanel = new JPanel(new GridBagLayout());

        GridBagConstraints preRight = new GridBagConstraints();
        preRight.anchor = GridBagConstraints.EAST;
        GridBagConstraints preLeft = new GridBagConstraints();
        preLeft.anchor = GridBagConstraints.WEST;
        preRight.weightx = 2.0;
        preRight.fill = GridBagConstraints.HORIZONTAL;
        preRight.gridwidth = GridBagConstraints.REMAINDER;

        firstPanel.add(usernameLabel,preLeft);
        firstPanel.add(chooseUsername,preRight);
        firstFrame.add(BorderLayout.CENTER, firstPanel);
        firstFrame.add(BorderLayout.SOUTH, enter);
        firstFrame.setVisible(true);
        firstFrame.setSize(500, 350);

        enter.addActionListener(new enterServerButtonListener());
    }
    public void chat(){
        newFrame.setVisible(true);
        JPanel southPanel = new JPanel();
        newFrame.add(BorderLayout.SOUTH, southPanel);
        southPanel.setLayout(new GridBagLayout());
        msgBox=new JTextField(35);
        sendMsg= new JButton("Send");
        chatBox=new JTextArea();
        chatBox.setEditable(false);
        newFrame.add(new JScrollPane(chatBox),BorderLayout.CENTER);

        chatBox.setLineWrap(true);

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.WEST;
        GridBagConstraints right = new GridBagConstraints();
        right.anchor = GridBagConstraints.EAST;
        right.weightx = 2.0;

        southPanel.add(msgBox,left );
        southPanel.add(sendMsg,right);

        chatBox.setFont(new Font("Veranda",Font.BOLD,12));
        sendMsg.addActionListener((new sendMessageButtonListener()));
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setSize(470,300);
    }

    class sendMessageButtonListener implements ActionListener{
        public void actionPerformed (ActionEvent event) {
            if(msgBox.getText().length() < 1 ) {
            }else if (msgBox.getText().length() < 1){
                    chatBox.setText("Cleared messages \n");
                    msgBox.setText("");
            }else {chatBox.append(("<" + username + ">:" +msgBox.getText()) + "\n");
            msgBox.setText("");
            }
        }
    }

    String username;

    class enterServerButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            username=chooseUsername.getText();
            if(username.length() <1) {
                System.out.println("Muie");}
                else if(chooseUsername.getText()=="Fane"){
                username= username + "Rege"; }
                else {
                firstFrame.setVisible(false);
                chat();
            }
        }
    }

}