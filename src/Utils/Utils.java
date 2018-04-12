package Utils;

import org.jetbrains.annotations.Contract;

public class Utils {

    /**
     * The multicast adress of our adhoc network
     */
    public static final String GroupAdress = "228.0.0.0";

    /**
     * The multicast port used by our adhoc network
     */
    public static final int GroupPort = 4445;

    public int computerNumber = 0;

    public static final int nullPacket = 0;
    public static final int tablePacket = 1;
    public static final int communicationPacket = 2;
    public static final int COMMUNICATION_HEADER_LENGTH = 12;

    public static int convertToInt(byte data){
        //Function to fix signed stuff.
        long dataL = (long) data;
        return (int)dataL & 0xff;
    }

    public static class Flags {
        public static final byte DATA = 1;
        public static final byte ACK = 2;
        public static final byte BROADCAST = 4;
    }


}
