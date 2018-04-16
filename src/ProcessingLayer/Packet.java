package ProcessingLayer;

import ApplicationLayer.Client;
import Exceptions.InvalidPacketFormat;
import TransportLayer.NetworkHandlerSender;
import Util.Utils;

import java.io.IOException;
import java.io.Serializable;
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
    private Client client;
    private static final long serialVersionUID = 7829136421241571165L;

    public Packet() {}

    public Packet(byte[] packet, Client client) throws InvalidPacketFormat {
        this(packet);
        this.client = client;
    }

    public Packet(byte[] packet) throws InvalidPacketFormat {
        packetType = packet[0];

        if(packetType == Utils.nullPacket) {

            throw new InvalidPacketFormat();

        } else {
            sourcePort = fromByteArray(packet, 1);

            destinationPort = fromByteArray(packet, 5);

            sequenceNumber = fromByteArray(packet, 9);

            acknowledgment = fromByteArray(packet, 13);

            ackFlag = packet[17];

            finFlag = packet[18];

            windowSize = fromByteArray(packet, 19);

            nextHop = packet[23];



            data = new byte[236];

           // System.out.println(packet.length);
            System.arraycopy(packet, 24, data, 0, packet.length - 24);
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

    public void receiveFromApplicationLayer(int destinationPort, int listeningPort, String message, MulticastSocket socket, int packetType) throws UnknownHostException {

          this.setPacketType((byte) packetType);
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


    public void receiveFromTransportLayer() throws IOException, ClassNotFoundException {
        //System.out.println(this.getData());

        String message = new String("Packet type: "+this.getPacketType()+ "\nSource port: " + this.getSourcePort()+ "\nDestination port: " + this.getDestinationPort()+
                "\nSequence number: " + this.getSequenceNumber()+ "\nAck: " + this.getAcknowledgment()+ "\nAckFlag: " + this.getAckFlag() +
                "\nFin flag: " + this.getFinFlag()+ "\nWindow Size: " + this.getWindowSize() + "\nNextHop: " + this.getNextHop() + "\nData: " + new String(this.getData()));
        sendToApplicationLayer(message);
    }

    public void sendToApplicationLayer(String message) {
        this.client.receiveFromProcessingLayer(message);
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
