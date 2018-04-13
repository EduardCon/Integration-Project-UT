package TransportLayer;


import ApplicationLayer.Client;
import ProcessingLayer.Packet;
import Util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import static ProcessingLayer.Packet.serialize;


/**
 * Object that handles outgoing packets.
 */
public class NetworkHandlerSender {

    /**
     * The address of the multicast group.
     */
    private InetAddress groupAddress;

    /**
     * The client's communication socket.
     */
    private MulticastSocket socket;

    /**
     * Constructor.
     */
    public NetworkHandlerSender(MulticastSocket socket) throws UnknownHostException {
        this.socket = socket;
        this.groupAddress = InetAddress.getByName(Utils.multiCastAddress);
    }

    /**
     */

    public void send(byte[] packet, int port) throws IOException {

        DatagramPacket toSend = new DatagramPacket(packet, packet.length, this.groupAddress, port);
        socket.send(toSend);
        System.out.println("Sent <" + packet + "> to " + this.groupAddress + " on port: " + port);
    }

    public void receiveFromProcessingLayer(Packet p) {
        try {
            int port = p.getDestinationPort();
            byte[] serializedPacket = serialize(p);
            this.send(serializedPacket, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter.
     * @return The client's communication socket.
     */
    public MulticastSocket getSocket() {
        return this.socket;
    }

}