package TransportLayer;


import ProcessingLayer.Packet;
import Util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


/**
 * Object that handles outgoing packets.
 */
public class NetworkHandlerSender {

    /**
     * The client that instantiated this object.
     */
    private Client client;

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
     * @param client The client that insantiates this object.
     */
    public NetworkHandlerSender(Client client) {
        this.client = client;
        this.socket = this.client.getSocket();
    }

    /**
     * Method for connecting to the multicast group.
     */
    public void connectToMultiCast() {
        try {

            this.groupAddress = InetAddress.getByName(Utils.multiCastAddress);
            this.socket.joinGroup(groupAddress);

        } catch (UnknownHostException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public Packet constructPacket(int destinationPort, byte[] message) {
        Packet packet = new Packet();
        packet.setSourcePort(this.client.getListeningPort());
        packet.setDestinationPort(destinationPort);
        packet.setSequenceNumber(0);
        packet.setAcknowledgment(0);
        packet.setAckFlag((byte) 0);
        packet.setFinFlag((byte) 0);
        packet.setWindowSize(10);
        packet.setNextHop((byte) 0);
        packet.setData(message);
        return packet;
    }
    /**
     * Method for sending a message.
     * @param message The message to be sent.
     * @param port The destination port.
     * @throws IOException
     */

    public void send(String message, int port) throws IOException {
        Packet packet = this.constructPacket(port,message.getBytes());

        DatagramPacket toSend = new DatagramPacket(message.getBytes(), message.length(), this.groupAddress, port);
        socket.send(toSend);
        System.out.println("Sent <" + message + "> to " + this.groupAddress + " on port: " + port);
    }

    /**
     * Getter.
     * @return The client's communication socket.
     */
    public MulticastSocket getSocket() {
        return this.socket;
    }

}