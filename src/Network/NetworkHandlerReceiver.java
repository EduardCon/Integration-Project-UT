package Network;

import Utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class NetworkHandlerReceiver extends Thread {
    private final String clientName = "";
    private MulticastSocket socket;
    private InetAddress groupAdress;
    private HashMap <Integer, String> connectedClients;
    private String clientIp;
    private Integer computerNumber;
    private String message = "";
    protected byte[] buf = new byte[256];

    public void run() {
       try {
           socket = new MulticastSocket(Utils.GroupPort);
           InetAddress group = InetAddress.getByName(Utils.GroupAdress);
           socket.joinGroup(group);
           while (true) {
               DatagramPacket packet = new DatagramPacket(buf, buf.length);
               socket.receive(packet);
               String received = new String(
                       packet.getData(), 0, packet.getLength());
               if ("end".equals(received)) {
                   break;
               }
           }
           socket.leaveGroup(group);
           socket.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
    }
}
