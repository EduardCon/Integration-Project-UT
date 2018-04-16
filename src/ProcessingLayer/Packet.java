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
    private byte packetType;
    private static final long serialVersionUID = 7829136421241571165L;

    public Packet() {}
 
    public Packet(byte[] packet) throws InvalidPacketFormat {
        packetType = packet[0];
        if(packetType == Utils.nullPacket) {
            throw new InvalidPacketFormat();
        } else if(packetType == Utils.tablePacket){
            //TODO
        } else if(packetType == Utils.communicationPacket) {
            sourcePort = fromByteArray(packet, 1);
            //sourcePort = packet[1];
            destinationPort = fromByteArray(packet, 5);
            //destination = packet[2];
            sequenceNumber = fromByteArray(packet, 9);
            //sequenceNumber = packet[3];
            acknowledgment = fromByteArray(packet, 13);
            //acknowledgment = packet[4];
            ackFlag = packet[17];
            //ackFlag = packet[5];
            finFlag = packet[18];
            //finFlag = packet[6];
            windowSize = fromByteArray(packet, 19);
            //windowSize = packet[7];
            nextHop = packet[23];
            //nextHop = packet[8];

            //int dataLength = (Utils.convertToInt((byte)((packet[9]<<8) + (Utils.convertToInt(packet[10])))));



            data = new byte[236];
            System.out.println(packet.length);
            System.arraycopy(packet, 24, data, 0, packet.length - 24);


//            if(dataLength!=0) {
//                System.arraycopy(packet, 9, data, 0, dataLength);
//            }
        }

    }

    public int fromByteArray(byte[] bytes, int i) {
        return bytes[i] << 24 | (bytes[i + 1] & 0xFF) << 16 | (bytes[i + 2] & 0xFF) << 8 | (bytes[i + 3] & 0xFF);
    }

    public byte[] toBytes(int x) {
        byte[] result = new byte[4];

        result[0] = (byte) (x >> 24);
        result[1] = (byte) (x >> 16);
        result[2] = (byte) (x >> 8);
        result[3] = (byte) (x);

        return result;
    }

    public byte[] getBytes() {
        byte[] arr = new byte[256];
        arr[0] = this.getPacketType();
        System.arraycopy(toBytes(this.getSourcePort()), 0, arr, 1, 4);
        System.arraycopy(toBytes(this.getDestinationPort()), 0, arr, 5, 4);
        System.arraycopy(toBytes(this.getSequenceNumber()), 0, arr, 9, 4);
        System.arraycopy(toBytes(this.getAcknowledgment()), 0, arr, 13, 4);
        arr[17] = this.getAckFlag();
        arr[18] = this.getFinFlag();
        System.arraycopy(toBytes(this.getWindowSize()), 0 , arr, 19, 4);
        arr[23] = this.getNextHop();
        System.arraycopy(this.getData(), 0, arr, 24, this.getData().length);

        return arr;
    }

    public void receiveFromApplicationLayer(int destinationPort, int listeningPort, byte[] message, MulticastSocket socket) throws UnknownHostException {
//        Packet packet = new Packet();
//        packet.setPacketType((byte) 1);
//        packet.setSourcePort(listeningPort);
//        packet.setDestinationPort(destinationPort);
//        packet.setSequenceNumber(0);
//        packet.setAcknowledgment(0);
//        packet.setAckFlag((byte) 0);
//        packet.setFinFlag((byte) 0);
//        packet.setWindowSize(10);
//        packet.setNextHop((byte) 0);
//        packet.setData(message);
//        this.sendToTransportLayer(packet, socket);
          this.setPacketType((byte) 2);
          this.setSourcePort(listeningPort);
          this.setDestinationPort(destinationPort);
          this.setSequenceNumber(0);
          this.setAcknowledgment(0);
          this.setAckFlag((byte) 0);
          this.setFinFlag((byte) 0 );
          this.setWindowSize(10);
          this.setNextHop((byte) 0);
          this.setData(message);
          this.sendToTransportLayer(this, socket);
    }

    public void sendToTransportLayer(Packet p, MulticastSocket socket) throws UnknownHostException {
        NetworkHandlerSender sender = new NetworkHandlerSender(socket);
        sender.receiveFromProcessingLayer(p);

    }

//    public static byte[] serialize(Object obj) throws IOException {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(bos);
//        oos.writeObject(obj);
//        oos.flush();
//        oos.close();
//        bos.close();
//        byte [] data = bos.toByteArray();
//        return data;
//    }

//    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
//        ByteArrayInputStream in = new ByteArrayInputStream(data);
//        ObjectInputStream is = new ObjectInputStream(in);
//        //is.close();
//        //in.close();
//        System.out.println("-------------------Deserialize");
//        System.out.println(in);
//        System.out.println(is);
//        System.out.println(is.readUTF());
//        return is.readUTF();
//    }


    public void receiveFromTransportLayer() throws IOException, ClassNotFoundException {
        System.out.println(this.getData());
        String message = new String("Packet type: "+this.getPacketType()+ "Source port: " + this.getSourcePort()+ "Destination port: " + this.getDestinationPort()+
                "Sequence number: " + this.getSequenceNumber()+ "Ack: " + this.getAcknowledgment()+ "AckFlag: " + this.getAckFlag() +
                "Fin flag: " + this.getFinFlag()+ "Window Size: " + this.getWindowSize() + "NextHop: " + this.getNextHop() + "Data: " + new String(this.getData()));
        sendToApplicationLayer(message);
    }

    public void sendToApplicationLayer(String message) {
        System.out.println(message);
    }

    public void print() {System.out.println(new String(this.getData())); }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getAcknowledgment() {
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

    public byte getPacketType() {
        return this.packetType;
    }

    public void setPacketType(byte packetType) {
        this.packetType = packetType;
    }
}
