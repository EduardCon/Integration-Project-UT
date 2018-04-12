package networkHandler;


import com.sun.org.apache.xpath.internal.operations.Mult;
import util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class NetworkHandlerSender {

    private Client client;
    private InetAddress groupAddress;
    private InetAddress myAddress;
    private MulticastSocket socket;

    public NetworkHandlerSender(Client client) {
        this.client = client;
        this.socket = this.client.getSocket();
    }

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

    public void send(String message, int port) throws IOException {
        DatagramPacket toSend = new DatagramPacket(message.getBytes(), message.length(), this.groupAddress, port);
        socket.send(toSend);
        System.out.println("Sent <" + message + "> to " + this.groupAddress + " on port: " + port);
    }

    public MulticastSocket getSocket() {
        return this.socket;
    }

}