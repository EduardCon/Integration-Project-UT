package TransportLayer;

import ApplicationLayer.Client;
import Exceptions.InvalidPacketFormat;
import ProcessingLayer.Packet;
import Util.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;


/**
 * The object that handles incoming packets.
 * Part of the Transport Layer.
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
     * The data buffer.
     */
    private byte[] buf = new byte[256];

    /**
     * The client object instantiating this object.
     */
    private Client client;

    /**
     * Constructor.
     * @param client The client instantiating this object.
     * @param socket The client's communication socket.
     */
    public NetworkHandlerReceiver(Client client, MulticastSocket socket) {
        try {
            this.client = client;
            this.socket = socket;
            groupAddress = InetAddress.getByName(Utils.multiCastAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        new Thread(this);
        this.start();
    }

    /**
     * Send data to the upper Processing Layer.
     * @param data The data to be sent.
     */
    public void sendToProcessingLayer(byte[] data) {
        try {
            Packet p = new Packet(data, this.client);
            p.receiveFromTransportLayer();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Thread method.
     * Constantly listening for packets.
     */
    public void run() {
        try {
            System.out.println("Listening...");
            while(true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                this.sendToProcessingLayer(packet.getData());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}