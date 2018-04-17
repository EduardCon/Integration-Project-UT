package ProcessingLayer;

import ApplicationLayer.Client;
import EncryptionLayer.Encryption;
import Exceptions.InvalidPacketFormat;
import TransportLayer.NetworkHandlerSender;
import Util.Utils;

import java.io.Serializable;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Packet implements Serializable{

    private byte[] data = new byte[128];
    private int sequenceNumber = 0;
    private int acknowledgment = 0;
    private byte destination = 0;
    private byte ackFlag = 0;
    private byte finFlag = 0;
    private byte nextHop = 0;
    private int windowSize = 0;
    private int sourcePort = 0;
    private int destinationPort = 0;
    private int dataLength;
    private byte packetType;
    private Client client;
    private static final String MAC_ALGORITHM = "HMACSHA256";
    private static final String HEX_AES_KEY =
            "B22E2B9A77C6DE2B9A779E7B2C6DA76E51C829E725EC8478A76E51C825EC8478";
    private static final String HEX_MAC_KEY = "AEB908AA1CEDFFDEA1F255640A05EEF6";

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

            dataLength = fromByteArray(packet, 24);

           // System.out.println(packet.length);
            System.arraycopy(packet, 28, data, 0, dataLength);
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
        System.arraycopy(toBytes(this.dataLength), 0, arr, 24, 4);
        System.arraycopy(this.getData(), 0, arr, 28, this.getData().length);

        return arr;
    }

    public void receiveFromApplicationLayer(int destinationPort, int listeningPort, String message, MulticastSocket socket, int packetType) throws Exception {
        //Encryption encryption = new Encryption();
        this.setPacketType((byte) packetType);
        this.setSourcePort(listeningPort);
        this.setDestinationPort(destinationPort);
        this.setSequenceNumber(0);
        this.setAcknowledgment(0);
        this.setAckFlag((byte) 0);
        this.setFinFlag((byte) 0 );
        this.setWindowSize(10);
        this.setNextHop((byte) 0);
        this.setDataLength(message.getBytes().length);
        this.setData(message);
           //this.setData(encryption.encrypt(message, HEX_AES_KEY, HEX_MAC_KEY, MAC_ALGORITHM));
        this.sendToTransportLayer(this, socket);
    }

    public void sendToTransportLayer(Packet p, MulticastSocket socket) throws UnknownHostException {
        NetworkHandlerSender sender = new NetworkHandlerSender(socket);
        sender.receiveFromProcessingLayer(p);

    }


    public void receiveFromTransportLayer() throws Exception {
        //System.out.println(this.getData());
        //System.out.println(this.getData().length);
        Encryption encryption = new Encryption();
        System.out.println(this.getData().length + " RECEIVED");
        String smallMessage = this.getMessage();
        String message = new String("Packet type: "+this.getPacketType()+ "\nSource port: " + this.getSourcePort()+ "\nDestination port: " + this.getDestinationPort()+
                "\nSequence number: " + this.getSequenceNumber()+ "\nAck: " + this.getAcknowledgment()+ "\nAckFlag: " + this.getAckFlag() +
                "\nFin flag: " + this.getFinFlag()+ "\nWindow Size: " + this.getWindowSize() + "\nNextHop: " + this.getNextHop() + "\nData: " +  this.getMessage()/**encryption.decrypt(new String(this.getData()), HEX_AES_KEY, HEX_MAC_KEY, MAC_ALGORITHM)*/);
        sendToApplicationLayer(smallMessage, this.getSourcePort() % 10);
    }

    public void sendToApplicationLayer(String message, int deviceNo) {
        this.client.receiveFromProcessingLayer(message, deviceNo);
    }

    public void print() {System.out.println(new String(this.getData())); }


    public String getMessage() {
        byte[] data = new byte[this.dataLength];
        System.arraycopy(this.data, 0, data, 0, this.dataLength);
        String message = new String(data);
        return message;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(String data) {
        byte[] stringData = data.getBytes();
        System.arraycopy(stringData, 0, this.data, 0, this.dataLength);
        for(int i = this.dataLength; i < 128; i++) {
            this.data[i] = (byte) 0;
        }
        //System.out.println(this.data.toString());
        //System.out.println(new String(this.data));
        System.out.println(this.data.length + " SENT");
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

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
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
