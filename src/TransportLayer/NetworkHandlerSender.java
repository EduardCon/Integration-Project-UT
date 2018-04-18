package TransportLayer;


import ProcessingLayer.Packet;
import Util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

//import static ProcessingLayer.Packet.serialize;


/**
 * Object that handles outgoing packets.
 * Part of the Transport Layer.
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
     * @param socket The socket on which it will listen.
     * @throws UnknownHostException
     */
    public NetworkHandlerSender(MulticastSocket socket) throws UnknownHostException {
        this.socket = socket;
        this.groupAddress = InetAddress.getByName(Utils.multiCastAddress);
    }

    /**
     * Method for sending a packet as a DatagramPacket.
     * @param packet The byte array containing the data.
     * @param port The destination port.
     * @throws IOException
     */
    public void send(byte[] packet, int port) throws IOException {

        DatagramPacket toSend = new DatagramPacket(packet, packet.length, this.groupAddress, port);
        socket.send(toSend);
        System.out.println("Sent <" + packet + "> to " + this.groupAddress + " on port: " + port);
    }

    /**
     * Method for receiving a packet from the upper Processing Layer.
     * @param p The packet received.
     */
    public void receiveFromProcessingLayer(Packet p) {
        try {
            int port = p.getDestinationPort();
            byte[] serializedPacket = p.getBytes();
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