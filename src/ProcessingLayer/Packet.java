package ProcessingLayer;

import Exceptions.InvalidPacketFormat;
import TransportLayer.NetworkHandlerSender;
import Util.Utils;

import java.io.*;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Packet implements Serializable{

    private byte[] data = new byte[0];
    private int sequenceNumber = 0;
    private int acknowledgment = 0;
    private byte destination = 0;
    private byte ackFlag = 0;
    private byte finFlag = 0;
    private byte nextHop = 0;
    private int windowSize = 0;
    private int sourcePort = 0;
    private int destinationPort = 0;
    private int packetType;


    public Packet() {}
 
    public Packet(byte[] packet) throws InvalidPacketFormat {
        packetType = packet[0];
        if(packetType == Utils.nullPacket) {
            throw new InvalidPacketFormat();
        } else if(packetType == Utils.tablePacket){
            //TODO
        } else if(packetType == Utils.communicationPacket) {
            sourcePort = packet[1];
            destination = packet[2];
            sequenceNumber = packet[3];
            acknowledgment = packet[4];
            ackFlag = packet[5];
            finFlag = packet[6];
            windowSize = packet[7];
            nextHop = packet[8];

            int dataLength = (Utils.convertToInt((byte)((packet[9]<<8) + (Utils.convertToInt(packet[10])))));

            data = new byte[100];

            if(dataLength!=0) {
                System.arraycopy(packet, 9, data, 0, dataLength);
            }
        }

    }

    public void receiveFromApplicationLayer(int destinationPort, int listeningPort, byte[] message, MulticastSocket socket) throws UnknownHostException {
        Packet packet = new Packet();
        packet.setSourcePort(listeningPort);
        packet.setDestinationPort(destinationPort);
        packet.setSequenceNumber(0);
        packet.setAcknowledgment(0);
        packet.setAckFlag((byte) 0);
        packet.setFinFlag((byte) 0);
        packet.setWindowSize(10);
        packet.setNextHop((byte) 0);
        packet.setData(message);
        this.sendToTransportLayer(packet, socket);
    }

    public void sendToTransportLayer(Packet p, MulticastSocket socket) throws UnknownHostException {
        NetworkHandlerSender sender = new NetworkHandlerSender(socket);
        sender.receiveFromProcessingLayer(p);

    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        oos.close();
        bos.close();
        byte [] data = bos.toByteArray();
        return data;
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        is.close();
        in.close();
        System.out.println("-------------------Deserialize");
        System.out.println(in);
        System.out.println(is);
        System.out.println(is.readObject());
        return is.readObject();
    }


    public void receiveFromTransportLayer(byte[] data) throws IOException, ClassNotFoundException {
        Packet p = (Packet) deserialize(data);
        sendToaApplicationLayer(p.getData().toString());
    }

    public void sendToaApplicationLayer(String message) {
        System.out.println(message);
    }

    public void print() {System.out.println(new String(this.getData())); }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getSequenceNumber(int i) {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getAcknowledgment(int i) {
        return acknowledgment;
    }

    public void setAcknowledgment(int acknowledgment) {
        this.acknowledgment = acknowledgment;
    }

    public byte getDestination() {
        return destination;
    }

    public void setDestination(byte destination) {
        this.destination = destination;
    }

    public byte getAckFlag() {
        return ackFlag;
    }

    public void setAckFlag(byte ackFlag) {
        this.ackFlag = ackFlag;
    }

    public byte getFinFlag() {
        return finFlag;
    }

    public void setFinFlag(byte finFlag) {
        this.finFlag = finFlag;
    }

    public byte getNextHop() {
        return nextHop;
    }

    public void setNextHop(byte nextHop) {
        this.nextHop = nextHop;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    public int getPacketType() {
        return packetType;
    }

    public void setPacketType(int packetType) {
        this.packetType = packetType;
    }
}
