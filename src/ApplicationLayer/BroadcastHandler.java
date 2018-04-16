package ApplicationLayer;

import ApplicationLayer.Client;
import ProcessingLayer.Packet;
import Util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class BroadcastHandler extends Thread {

    private int groupPort;
    private MulticastSocket groupMulticastSocket;
    private InetAddress groupAddress;
    private final String message;
    private Client client;

    public BroadcastHandler(Client client) {
        this.client = client;
        this.groupPort = Utils.multiCastGroupPort;
        try {
            this.groupAddress = InetAddress.getByName(Utils.multiCastAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.groupMulticastSocket = this.client.getGroupSocket();
        message = "Client " + this.client.getName() + " is broadcasting on group port: " + Utils.multiCastGroupPort +
        " with listening port: " + this.client.getListeningPort();

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

    public void sendToProcessingLayer(String message) {
        Packet packet = new Packet();
        try {
            packet.receiveFromApplicationLayer(Utils.multiCastGroupPort, this.client.getListeningPort(), message.getBytes(), this.client.getSocket(), 1);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}