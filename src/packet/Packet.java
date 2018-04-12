package packet;

import exceptions.InvalidPacketFormat;
import util.Utils;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;

public class Packet {

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



    public static void main (String[] args) throws InvalidPacketFormat {
        byte[] packetel= new byte[10000000];
        packetel[0] = 2;
        String message = "Salut ";
        String message1 = "Boss!";
        byte[] arr = message.getBytes();
        byte[] arr1 = message1.getBytes();
        System.arraycopy(arr, 0, packetel, 9, arr.length);
        Packet pack = new Packet(packetel);
        pack.print();
    }

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

    public int getPacketType() {
        return packetType;
    }

    public void setPacketType(int packetType) {
        this.packetType = packetType;
    }
}
