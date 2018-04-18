package ApplicationLayer;

import ProcessingLayer.Packet;
import TransportLayer.NetworkHandlerReceiver;
import TransportLayer.NetworkHandlerSender;
import Util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * Class for the Client object used for connecting to the multicast group.
 */
public class Client extends Observable {

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
     * The buffer that stores incoming messages.
     */
    private Map<Integer, List<String>> receivedBuffer;

    public String lastMessageTodDisplay;

    public Client() {

    }

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

    /**
     * The InetAddress of the group.
     */
    private InetAddress groupAddress;

    /**
     * Routing table object.
     */
    private RoutingTable routingTable;

    /**
     * Client constructor.
     * @param name The name of the Client.
     */
    public Client(String name, int listeningPort) {
        this.name = name;
        this.deviceNo = listeningPort % 10;
        this.listeningPort = listeningPort;
        receivedBuffer = new HashMap<>();

    }

    /**
     * Connects the Client to the multicast group.
     * Instantiates the objects that handle communication.
     * Instantiates the routing table object.
     */
    public void connect() {
        String s = name.concat(" has joined the channel");
        List<String> joined = new ArrayList<>();
        joined.add(s);
        receivedBuffer.put(0,joined);
        try {
            //Create a new socket for this client's listening port.
            this.socket = new MulticastSocket(this.getListeningPort());

            //Get the multicast group address.
            this.groupAddress = InetAddress.getByName(Utils.multiCastAddress);
            //Create a new socket for the group's listening port.
            this.groupSocket = new MulticastSocket(Utils.multiCastGroupPort);
            //Join the multicast group using the group's socket.
            this.groupSocket.joinGroup(InetAddress.getByName(Utils.multiCastAddress));

            //Join the multicast group using the client's socket.
            this.socket.joinGroup(groupAddress);

            this.receiver = new NetworkHandlerReceiver(this, this.socket);

            this.routingTable = new RoutingTable(this);

        } catch (IOException e) {
            e.printStackTrace();
        }



        System.out.println("Client " + this.getName() + " has port: " + this.getListeningPort() + " and number: " + this.deviceNo);

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
     * Method for sending a message to the processing layer.
     * @param message The message to be sent.
     * @param port The destination port.
     * @throws IOException
     */
    public void sendToProceessingLayer(String message, int port) throws Exception {
        Packet packet = new Packet();
        packet.receiveFromApplicationLayer(port, listeningPort, message, this.socket, 2) ;
    }


    /**
     * Method for receiving a message from the processing layer and adding it to the buffer.
     * @param message The message received.
     * @param deviceNo The device who sent it.
     */
    public void receiveFromProcessingLayer(String message, int deviceNo) {


        System.out.println("\n-------------- RECEIVED MESSAGE --------------\n");

        List<String> list = receivedBuffer.get(deviceNo);
        if(list == null) {
            receivedBuffer.put(deviceNo, list = new ArrayList<>());
        }
        list.add(message);
        lastMessageTodDisplay = this.name + ": " + message;
        setChanged();
        notifyObservers();

        System.out.println(receivedBuffer);
        System.out.println("\n--------------- END OF DETAILS -----------------\n");
    }

    /**
     * Getter.
     * @return The listening port.
     */
    public int getListeningPort() {
        return this.listeningPort;
    }

    /**
     * Getter.
     * @return The routing table.
     */
    public RoutingTable getRoutingTable() {
        return this.routingTable;
    }

    /**
     * Getter.
     * @return The buffer.
     */
    public Map<Integer, List<String>> getReceivedBuffer() {
        return this.receivedBuffer;
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

    /**
     * Setter.
     * @param name The client's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter.
     * @param deviceNo The device number.
     */
    public void setDeviceNo(int deviceNo) {this.deviceNo = deviceNo;}

    /**
     * Setter.
     * @param listeningPort The listening port.
     */
    public void setListeningPort(int listeningPort) {
        this.listeningPort = listeningPort;
    }
}

