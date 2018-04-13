package TransportLayer;

import Util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class BroadcastHandler extends Thread {

    private Client client;
    private int groupPort;
    private MulticastSocket groupMulticastSocket;
    private InetAddress groupAddress;
    private String message;


    public BroadcastHandler(Client client) {
        this.client = client;
        this.groupPort = Utils.multiCastGroupPort;
        try {
            this.groupAddress = InetAddress.getByName(Utils.multiCastAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.groupMulticastSocket = this.client.getGroupSocket();
        message = "Client " + this.client.getName() + " is connected on port: " + this.client.getListeningPort();

        new Thread(this);
        this.start();
    }

    public void run() {
        DatagramPacket broadcast = new DatagramPacket(this.message.getBytes(), this.message.length(), this.groupAddress, this.groupPort);
        try {
            this.groupMulticastSocket.send(broadcast);
            TimeUnit.SECONDS.sleep(1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
