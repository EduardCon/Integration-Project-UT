package util;

public class Utils {

    public static final int multiCastGroupPort = 4464;

    public static final String multiCastAddress = "228.0.0.0";

    public static final int nullPacket = 0;
    public static final int tablePacket = 1;
    public static final int communicationPacket = 2;
    public static final int COMMUNICATION_HEADER_LENGTH = 12;

    public static int convertToInt(byte data){
        //Function to fix signed stuff.
        long dataL = (long) data;
        return (int)dataL & 0xff;
    }


}
