package Network;

import Utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class NetworkHandlerSender {
    private final String clientName = "";
    private MulticastSocket socket;
    private InetAddress groupAdress;
    private HashMap <Integer, String> connectedClients;
    private String clientIp;
    private Integer computerNumber;
    private String message = "mesaj 1";

    public static void main(String[] args) {
        new NetworkHandlerSender("Eduard");
        new NetworkHandlerReceiver();
    }

    public NetworkHandlerSender(String clientName) {
        clientName = this.clientName;

        try {
            groupAdress = InetAddress.getByName(Utils.GroupAdress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        connectedClients = new HashMap<>();

        try {
            socket = new MulticastSocket(Utils.GroupPort);
            socket.joinGroup(groupAdress);
            DatagramPacket test = new DatagramPacket(message.getBytes(), message.length(), groupAdress, Utils.GroupPort);
            socket.send(test);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
