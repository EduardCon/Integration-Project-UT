package ApplicationLayer;

import ApplicationLayer.Client;
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
        message = "Client " + this.client.getName() + " is broadcasting on group port: " + Utils.multiCastGroupPort;

        new Thread(this);
        this.start();
    }

    public void run() {
        try {
            while(true) {
                System.out.println("Sending broadcast!");
                this.client.sendToProceessingLayer(this.message, Utils.multiCastGroupPort);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
