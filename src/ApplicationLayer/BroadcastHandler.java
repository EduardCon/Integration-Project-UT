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

/**
 * Class that handles periodic broadcasts of a client's routing table.
 * Part of the Application Layer.
 */
public class BroadcastHandler extends Thread {

    /**
     * The group port.
     */
    private int groupPort;

    /**
     * The InetAddress of the group.
     */
    private InetAddress groupAddress;

    /**
     * The message to be broadcasted.
     */
    private String message;

    /**
     * The routing table.
     */
    private RoutingTable routingTable;

    /**
     * The listening port of the broadcast.
     */
    private int listeningPort;

    /**
     * The client's communication socket.
     */
    private MulticastSocket clientMulticastSocket;


    /**
     * Constructo.
     * @param routingTable The client's routing table.
     */
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


    /**
     * Override of the Thread method.
     * Each 5 seconds sends a broadcast.
     */
    public void run() {
        try {
            System.out.println("Sending broadcast!");
            while(true) {
                this.sendToProcessingLayer(this.message);
                TimeUnit.SECONDS.sleep(2);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /** Setter.
     * @param message The message to be broadcasted.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sends the message further to the Processing Layer.
     * @param message The message to be sent.
     */
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