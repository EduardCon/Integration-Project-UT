package ProcessingLayer;

import ApplicationLayer.Client;
import EncryptionLayer.Encryption;
import Exceptions.InvalidPacketFormat;
import TransportLayer.NetworkHandlerSender;
import Util.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

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

    public void receiveFromApplicationLayer(int destinationPort, int listeningPort, String message, MulticastSocket socket, int packetType) throws UnknownHostException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException, InvalidParameterSpecException {
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
          //this.setData(encryption.encrypt(message).getBytes());
            this.setData(message.getBytes());
          this.sendToTransportLayer(this, socket);
    }

    public void sendToTransportLayer(Packet p, MulticastSocket socket) throws UnknownHostException {
        NetworkHandlerSender sender = new NetworkHandlerSender(socket);
        sender.receiveFromProcessingLayer(p);

    }


    public void receiveFromTransportLayer() throws IOException, ClassNotFoundException, NoSuchPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidParameterSpecException, InvalidAlgorithmParameterException {
        //System.out.println(this.getData());
        Encryption encryption = new Encryption();
        System.out.println(this.getData().length + " RECEIVED");
        String message = new String("Packet type: "+this.getPacketType()+ "\nSource port: " + this.getSourcePort()+ "\nDestination port: " + this.getDestinationPort()+
                "\nSequence number: " + this.getSequenceNumber()+ "\nAck: " + this.getAcknowledgment()+ "\nAckFlag: " + this.getAckFlag() +
                "\nFin flag: " + this.getFinFlag()+ "\nWindow Size: " + this.getWindowSize() + "\nNextHop: " + this.getNextHop() + "\nData: " +  this.getMessage() /*encryption.decrypt(new String(this.getData()))*/ );
        sendToApplicationLayer(message);
    }

    public void sendToApplicationLayer(String message) {
        this.client.receiveFromProcessingLayer(message);
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

    public void setData(byte[] data) {
        //this.data = data;
        System.arraycopy(data, 0, this.data, 0, this.dataLength);
        for(int i = this.dataLength; i < 128; i++) {
            this.data[i] = (byte) 0;
        }
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
