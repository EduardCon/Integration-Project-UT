package networkHandler;

import util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class NetworkHandlerReceiver extends Thread {

    private Client client;
    private MulticastSocket socket;
    private InetAddress groupAddress;
    private InetAddress myAddress;
    private byte[] buf = new byte[256];

    public NetworkHandlerReceiver(Client client, MulticastSocket socket) {
        this.client = client;
        this.socket = socket;
        new Thread(this);
        this.start();
    }

    public void run() {
        try {
            System.out.println("Listening...");
            while(true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: <" + received + "> from: " + packet.getAddress() + " on port: " + packet.getPort());
                if("end".equals(received)) {
                    this.socket.leaveGroup(InetAddress.getByName(Utils.multiCastAddress));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}