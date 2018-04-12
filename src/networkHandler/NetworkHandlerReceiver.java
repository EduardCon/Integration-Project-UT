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
    private MulticastSocket groupSocket;
    private InetAddress groupAddress;
    private InetAddress myAddress;
    private byte[] buf = new byte[256];

    public NetworkHandlerReceiver(Client client) {
        this.client = client;
        try {
            this.socket = this.client.getSocket();
            this.groupSocket = this.client.getGroupSocket();
            groupAddress = InetAddress.getByName(Utils.multiCastAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

                DatagramPacket broadcastPacket = new DatagramPacket(buf, buf.length);
                groupSocket.receive(broadcastPacket);
                String receivedBroadcast = new String(broadcastPacket.getData(), 0, broadcastPacket.getLength());
                System.out.println("Received broadcast: <" + receivedBroadcast + "> from " + broadcastPacket.getAddress());
                if("end".equals(received)) {
                    break;
                }
            }
            socket.leaveGroup(groupAddress);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}