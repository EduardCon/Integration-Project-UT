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
    private byte[] buf = new byte[256];

    private Client client;

    /**
     * Constructor.
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

    public void sendToProcessingLayer(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        try {
            Packet p = new Packet(data, this.client);
            p.receiveFromTransportLayer();
        } catch (IOException | ClassNotFoundException | InvalidPacketFormat e) {
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
                this.sendToProcessingLayer(packet.getData());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

}