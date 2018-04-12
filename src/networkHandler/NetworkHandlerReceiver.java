package networkHandler;

import com.sun.org.apache.xpath.internal.operations.Mult;
import util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


/**
 * The object that handles incoming packets.
 */
public class NetworkHandlerReceiver extends Thread {

    /**
     * The client that instantiated this object.
     */
    private Client client;

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

    /**
     * Constructor.
     * @param client The client instantiating this object.
     */
    public NetworkHandlerReceiver(Client client, MulticastSocket socket) {
        this.client = client;
        try {
            this.socket = socket;
            groupAddress = InetAddress.getByName(Utils.multiCastAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(this);
        this.start();
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
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: <" + received + "> from: " + packet.getAddress() + " on port: " + packet.getPort());
                if("end".equals(received)) {
                    socket.leaveGroup(groupAddress);
                    socket.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}