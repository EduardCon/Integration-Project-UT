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

    /**
     * Method for sending a message.
     * @param message The message to be sent.
     * @param port The destination port.
     * @throws IOException
     */

    public void send(Packet p) throws IOException {
        DatagramPacket toSend = new DatagramPacket(message.getBytes(), message.length(), this.groupAddress, port);
        socket.send(toSend);
        System.out.println("Sent <" + message + "> to " + this.groupAddress + " on port: " + port);
    }

    public void receiveFromProcessingLayer(Packet p) {
        try {
            this.send(p);
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