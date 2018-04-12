package networkHandler;

import com.sun.org.apache.xpath.internal.operations.Mult;
import util.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Client {

    private String name;
    private int deviceNo;
    private NetworkHandlerReceiver receiver;
    private NetworkHandlerSender sender;
    private BroadcastHandler broadcast;
    private int listeningPort;
    private MulticastSocket socket;
    private MulticastSocket groupSocket;

    public static final void main(String[] args) {
        Client c = new Client("edi");
        c.findClientPort();
        c.connect();
        try {
            c.send("what", 54322);
            c.send("the", 54322);
            c.send("fuck", 54321);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Client(String name) {
        this.name = name;
    }

    public void connect() {
        this.listeningPort = this.findClientPort();
        try {
            this.socket = new MulticastSocket(this.listeningPort);
            this.groupSocket = new MulticastSocket(Utils.multiCastGroupPort);
            this.groupSocket.joinGroup(InetAddress.getByName(Utils.multiCastAddress));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.sender = new NetworkHandlerSender(this);
        this.sender.connectToMultiCast();
        this.receiver = new NetworkHandlerReceiver(this);
        //this.broadcast = new BroadcastHandler(this);

        this.deviceNo = this.listeningPort % 10;
        System.out.println("Client " + this.name + " has port: " + this.listeningPort + " and number: " + this.deviceNo);

    }

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

    public int getListeningPort() {
        return this.listeningPort;
    }

    public void send(String message, int port) throws IOException {
        this.sender.send(message, port);
    }


    public NetworkHandlerSender getSender() {
        return sender;
    }

    public String getName() {
        return this.name;
    }

    public int getDeviceNo() {
        return this.deviceNo;
    }

    public MulticastSocket getSocket() {
        return this.socket;
    }

    public MulticastSocket getGroupSocket() {
        return this.groupSocket;
    }

    public void setDeviceNo(int deviceNo) {
        this.deviceNo = deviceNo;
    }
}
