package multicastTest;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Multicast {

    public final static void main(String[] args) throws UnknownHostException, IOException {
        String msg = "test";
        InetAddress group = InetAddress.getByName("228.0.0.0");
        MulticastSocket s = new MulticastSocket(6969);
        s.joinGroup(group);

        DatagramPacket test = new DatagramPacket(msg.getBytes(), msg.length(), group, 6969);

        s.send(test);

        while(true) {
            byte[] buf = new byte[1000];
            DatagramPacket response = new DatagramPacket(buf, buf.length);
            s.receive(response);
            if(response.getLength() > 0) {
                String f = new String(response.getData());
                System.out.println(f);
                //break;
            }
        }


    }
}