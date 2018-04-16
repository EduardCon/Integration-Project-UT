package ApplicationLayer;

import ProcessingLayer.Packet;
import TransportLayer.NetworkHandlerReceiver;
import TransportLayer.NetworkHandlerSender;
import Util.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Class for the Client object used for connecting to the multicast group.
 */
public class Client {

    /**
     * The name of the Client.
     */
    private String name;

    /**
     * The number of the device (between 1 and 4).
     */
    private int deviceNo;

    /**
     * The object that handles incoming packets.
     */
    private NetworkHandlerReceiver receiver;

    /**
     * The object that handles outgoing packets.
     */
    private NetworkHandlerSender sender;

    /**
     * The object that handles periodic broadcasting.
     */
    private BroadcastHandler broadcastSender;

    /**
     * The object that handles incoming broadcasts.
     */
    private NetworkHandlerReceiver broadcastReceiver;

    /**
     * The port that this client is listening to.
     */
    private int listeningPort;

    /**
     * The socket used for communicating with this client.
     */
    private MulticastSocket socket;

    /**
     * The socket used for communicating with the whole multicast group.
     */
    private MulticastSocket groupSocket;

    private InetAddress groupAddress;

    /**
     * Client constructor.
     * @param name The name of the Client.
     */
    public Client(String name) {
        this.name = name;
        this.listeningPort = this.findClientPort();
        this.deviceNo = this.listeningPort % 10;
    }

    /**
     * Connects the Client to the multicast group.
     * Instantiates the objects that handle communication.
     */
    public void connect() {

        try {
            //Create a new socket for this client's listening port.
            this.socket = new MulticastSocket(this.listeningPort);

            //Get the multicast group address.
            this.groupAddress = InetAddress.getByName(Utils.multiCastAddress);
            //Create a new socket for the group's listening port.
            this.groupSocket = new MulticastSocket(Utils.multiCastGroupPort);
            //Join the multicast group using the group's socket.
            this.groupSocket.joinGroup(InetAddress.getByName(Utils.multiCastAddress));

            //Join the multicast group using the client's socket.
            this.socket.joinGroup(groupAddress);

            this.receiver = new NetworkHandlerReceiver(this.socket);
            //this.broadcastSender = new BroadcastHandler(this);
            //this.broadcastReceiver = new NetworkHandlerReceiver(this.groupSocket);

        } catch (IOException e) {
            e.printStackTrace();
        }



        System.out.println("Client " + this.name + " has port: " + this.listeningPort + " and number: " + this.deviceNo);

    }

    /**
     * Search through the interfaces and find an IP address that has a "192.168.5.X" prefix.
     * X is the computer number and is used for the port.
     * The port will be 5432X.
     * @return The client's port.
     */
    public int findClientPort() {
        String ip;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    ip = addr.getHostAddress().toString();
                    if(ip.startsWith("192.168.5.")) {
                        char suffix = ip.charAt(10);
                        int foundPort = 54320 + (suffix - '0');
                        return foundPort;
                    }
                }
            }
            //error here
            return -1;
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Method for sending a message.
     * @param message The message to be sent.
     * @param port The destination port.
     * @throws IOException
     */
    public void sendToProceessingLayer(String message, int port) throws IOException {
        Packet packet = new Packet();
        packet.receiveFromApplicationLayer(port, listeningPort, message.getBytes(), this.socket) ;
    }

    public void receiveFromProcessingLayer(String message) {
        System.out.println(message);
    }

    /**
     * Get the listening port.
     * @return The listening port.
     */
    public int getListeningPort() {
        return this.listeningPort;
    }



    /**
     * Getter.
     * @return The object that handles outgoing packets.
     */
    public NetworkHandlerSender getSender() {
        return sender;
    }

    /**
     * Getter.
     * @return The name of the client.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter.
     * @return The number of this client.
     */
    public int getDeviceNo() {
        return this.deviceNo;
    }

    /**
     * Getter.
     * @return This client's communication socket.
     */
    public MulticastSocket getSocket() {
        return this.socket;
    }

    /**
     * Getter.
     * @return The multicast group communication socket.
     */
    public MulticastSocket getGroupSocket() {
        return this.groupSocket;
    }

}
