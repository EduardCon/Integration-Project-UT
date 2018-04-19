package ProcessingLayer;

import ApplicationLayer.Client;
import EncryptionLayer.Encryption;
import Exceptions.InvalidPacketFormat;
import TransportLayer.NetworkHandlerSender;
import Util.Utils;

import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Class that handles processing the incoming and outgoing data.
 * Part of the Processing Layer.
 */

public class Packet {

    /**
     * The data has a maximum size of 128 bytes.
     */
    private byte[] data = new byte[128];

    /**
     * The sequence number of the packet.
     */
    private int sequenceNumber = 0;

    /**
     * The acknowledgement number of the packet.
     */
    private int acknowledgment = 0;

    /**
     * remove?
     */
    private byte destination = 0;

    /**
     * Acknowledgement flag.
     */
    private byte ackFlag = 0;

    /**
     * Finish flag.
     */
    private byte finFlag = 0;

    /**
     * Next hop.
     */
    private byte nextHop = 0;

    /**
     * Window size.
     */
    private int windowSize = 0;

    /**
     * Source port of the client.
     */
    private int sourcePort = 0;

    /**
     * Destination port.
     */
    private int destinationPort = 0;

    /**
     * The length of the data.
     */
    private int dataLength;

    /**
     * Packet type.
     */
    private byte packetType;

    /**
     * Client who sent / needs to receive this packet.
     * Used to forward towards the Application Layer.
     */
    private Client client;

    /**
     * Constructor for an empty packet.
     */
    public Packet() {

    }


    /**
     * Constructor with client.
     * @param packet Array of bytes which will construct the packet.
     * @param client The client.
     * @throws InvalidPacketFormat If the packet is invalid.
     */
    public Packet(byte[] packet, Client client) throws InvalidPacketFormat {
        this(packet);
        this.client = client;
    }

    /**
     * Constructor without a client.
     * @param packet Array of bytes which will construct the packet.
     * @throws InvalidPacketFormat
     */
    public Packet(byte[] packet) throws InvalidPacketFormat {
        packetType = packet[0];

        if(packetType == Utils.nullPacket) {
            //throw new InvalidPacketFormat("Packet is invalid!");
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

            System.arraycopy(packet, 28, data, 0, dataLength);
        }
    }

    /**
     * Converts a 4 consecutive bytes from a byte array to a integer.
     * @param bytes The byte array to be converted.
     * @param i The position of the array from where we will start converting.
     * @return The converted array.
     */
    public int fromByteArray(byte[] bytes, int i) {
        return bytes[i] << 24 | (bytes[i + 1] & 0xFF) << 16 | (bytes[i + 2] & 0xFF) << 8 | (bytes[i + 3] & 0xFF);
    }

    /**
     * Convert an integer to an array of bytes.
     * @param x The integer to be converted.
     * @return The byte array representation.
     */
    public byte[] toBytes(int x) {
        byte[] result = new byte[4];

        result[0] = (byte) (x >> 24);
        result[1] = (byte) (x >> 16);
        result[2] = (byte) (x >> 8);
        result[3] = (byte) (x);

        return result;
    }

    /**
     *
     * @return
     */
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

    /**
     * Method for receiving a message from the upper Application Layer.
     * It processes the message into a packet and forwards it to the lower Transport Layer.
     * @param destinationPort The destination port of the message.
     * @param listeningPort The listening port of the client (acts as a source port of the message).
     * @param message The message to be sent.
     * @param socket The client's communication socket.
     * @param packetType This packet's type.
     * @throws Exception
     */
    public void receiveFromApplicationLayer(int destinationPort, int listeningPort, String message, MulticastSocket socket, int packetType) throws Exception {
        Encryption encryption = new Encryption();
        this.setPacketType((byte) packetType);
        this.setSourcePort(listeningPort);
        this.setDestinationPort(destinationPort);
        this.setSequenceNumber(0);
        this.setAcknowledgment(0);
        this.setAckFlag((byte) 0);
        this.setFinFlag((byte) 0 );
        this.setWindowSize(10);
        this.setNextHop((byte) (listeningPort % 10));
        this.setDataLength(message.getBytes().length);
        //this.setData(encryption.encrypt(message));
        this.setData(message);
        this.sendToTransportLayer(this, socket);
    }

    /**
     * Sends a packet to the lower Transport Layer.
     * @param p The packet to be sent.
     * @param socket The client's communication socket.
     * @throws UnknownHostException
     */
    public void sendToTransportLayer(Packet p, MulticastSocket socket) throws UnknownHostException {
        NetworkHandlerSender sender = new NetworkHandlerSender(socket);
        sender.receiveFromProcessingLayer(p);

    }

    /**
     * Method for receiving a packet from the lower Transport Layer.
     * It extracts the message and forwards it to the upper Application Layer to the Client or to the RoutingTable.
     */
    public void receiveFromTransportLayer() throws Exception{
        if(this.packetType == 2) {
            //Encryption encryption = new Encryption();
            String smallMessage = this.getMessage();
            sendToApplicationLayer(smallMessage, this.getSourcePort() % 10);
        } else if(this.packetType == 1) {
            sendToRoutingTable(this);
        }
    }

    /**
     * Sends a packet to the upper Application Layer, to the Routing Table.
     * @param packet The packet to be sent.
     */
    public void sendToRoutingTable(Packet packet) {
        this.client.getRoutingTable().receiveFromProcessingLayer(packet);
    }

    /**
     * Send a message to the upper Application Layer, to the Client.
     * @param message The message to be sent.
     * @param deviceNo The device number of the sender.
     */
    public void sendToApplicationLayer(String message, int deviceNo) {
        this.client.receiveFromProcessingLayer(message, deviceNo);
    }

    /**
     * Method for printing the data of this packet.
     */
    public void print() {System.out.println(new String(this.getData())); }


    /**
     * Getter.
     * @return The message from the packet.
     */
    public String getMessage() {
        byte[] data = new byte[this.dataLength];
        System.arraycopy(this.data, 0, data, 0, this.dataLength);
        String message = new String(data);
        return message;
    }

    /**
     * Getter.
     * @return The data as an array of bytes.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Setter.
     * @param data The data as an array of bytes.
     */
    public void setData(String data) {
        byte[] stringData = data.getBytes();
        System.arraycopy(stringData, 0, this.data, 0, this.dataLength);
        for(int i = this.dataLength; i < 128; i++) {
            this.data[i] = (byte) 0;
        }
    }

    /**
     * Getter.
     * @return The sequence number of the packet.
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Setter.
     * @param sequenceNumber The new sequence number.
     */
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Getter.
     * @return The acknowledgement number of the packet.
     */
    public int getAcknowledgment() {
        return acknowledgment;
    }

    /**
     * Setter.
     * @param acknowledgment The new acknowledgement number.
     */
    public void setAcknowledgment(int acknowledgment) {
        this.acknowledgment = acknowledgment;
    }

    /**
     * Getter.
     * @return The destination device.
     */
    public byte getDestination() {
        return destination;
    }

    /**
     * Setter.
     * @param destination The new destination.
     */
    public void setDestination(byte destination) {
        this.destination = destination;
    }

    /**
     * Setter.
     * @param dataLength The new data length.
     */
    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    /**
     * Getter.
     * @return The acknowledgement flag.
     */
    public byte getAckFlag() {
        return ackFlag;
    }

    /**
     * Setter.
     * @param ackFlag The new acknowledgement flag.
     */
    public void setAckFlag(byte ackFlag) {
        this.ackFlag = ackFlag;
    }

    /**
     * Getter.
     * @return The final flag.
     */
    public byte getFinFlag() {
        return finFlag;
    }

    /**
     * Setter.
     * @param finFlag The new final flag.
     */
    public void setFinFlag(byte finFlag) {
        this.finFlag = finFlag;
    }

    /**
     * Getter.
     * @return The next hop.
     */
    public byte getNextHop() {
        return nextHop;
    }

    /**
     * Setter
     * @param nextHop The new next hop.
     */
    public void setNextHop(byte nextHop) {
        this.nextHop = nextHop;
    }

    /**
     * Getter.
     * @return The window size.
     */
    public int getWindowSize() {
        return windowSize;
    }

    /**
     * Setter.
     * @param windowSize The new window szie.
     */
    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    /**
     * Getter.
     * @return The source port.
     */
    public int getSourcePort() {
        return sourcePort;
    }

    /**
     * Setter.
     * @param sourcePort The new source port.
     */
    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    /**
     * Getter.
     * @return The destination port.
     */
    public int getDestinationPort() {
        return destinationPort;
    }

    /**
     * Setter.
     * @param destinationPort The new destination port.
     */
    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    /**
     * Getter.
     * @return The packet type.
     */
    public byte getPacketType() {
        return this.packetType;
    }

    /**
     * Setter.
     * @param packetType The new packet type.
     */
    public void setPacketType(byte packetType) {
        this.packetType = packetType;
    }
}
