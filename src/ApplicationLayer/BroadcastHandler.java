package ApplicationLayer;

import ProcessingLayer.Packet;
import Util.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.concurrent.TimeUnit;

public class BroadcastHandler extends Thread {

    private int groupPort;
    private InetAddress groupAddress;
    private String message;
    private RoutingTable routingTable;
    private int listeningPort;
    private MulticastSocket clientMulticastSocket;

    public BroadcastHandler(RoutingTable routingTable) {
        this.routingTable = routingTable;
        this.groupPort = Utils.multiCastGroupPort;
        this.clientMulticastSocket = this.routingTable.getClient().getSocket();
        this.listeningPort = this.routingTable.getClient().getListeningPort();
        this.message = "";
        try {
            this.groupAddress = InetAddress.getByName(Utils.multiCastAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //message = "Client " + this.routingTable.getClient().getName() + " is broadcasting on group port: " + Utils.multiCastGroupPort +
        //" with listening port: " + this.routingTable.getClient().getListeningPort();

        new Thread(this);
        this.start();
    }

    public void run() {
        try {
            System.out.println("Sending broadcast!");
            while(true) {
                this.sendToProcessingLayer(this.message);
                TimeUnit.SECONDS.sleep(5);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void sendToProcessingLayer(String message) {
        Packet packet = new Packet();
        try {
            if(message.length() == 0) {
                packet.receiveFromApplicationLayer(this.groupPort, this.listeningPort, message, this.clientMulticastSocket, 0);
            } else {
                packet.receiveFromApplicationLayer(this.groupPort, this.listeningPort, message, this.clientMulticastSocket, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}