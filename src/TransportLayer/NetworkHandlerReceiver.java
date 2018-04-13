package TransportLayer;

import ApplicationLayer.Client;
import ProcessingLayer.Packet;
import Util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


/**
 * The object that handles incoming packets.
 */
public class NetworkHandlerReceiver extends Thread {

    /**
     * The client's communication socket.
     */
    private MulticastSocket socket;

    /**
     * The address of the multicast group.
     */
    private InetAddress groupAddress;

    /**
     * Buffer.
     */
    private byte[] buf = new byte[2048];

    /**
     * Constructor.
     */
    public NetworkHandlerReceiver(MulticastSocket socket) {
        try {
            this.socket = socket;
            groupAddress = InetAddress.getByName(Utils.multiCastAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        new Thread(this);
        this.start();
    }

    public void sendToProcessingLayer(byte[] packet) {
        try {
            new Packet();
            Packet p = (Packet) Packet.deserialize(packet);
            p.receiveFromTransportLayer(packet);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    /**
     * Thread method.
     */

    public void run() {
        try {
            System.out.println("Listening...");
            while(true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                System.out.println("RECEIVED");
                this.sendToProcessingLayer(packet.getData());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}