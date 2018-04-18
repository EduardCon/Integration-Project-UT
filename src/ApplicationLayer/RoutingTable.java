package ApplicationLayer;

import ProcessingLayer.Packet;
import TransportLayer.NetworkHandlerReceiver;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.scene.control.Tab;
import javafx.scene.text.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for the routing table.
 * Part of the Application Layer.
 */
public class RoutingTable {

    /**
     * The client instantiating this table.
     */
    private Client client;

    /**
     * The broadcast handler.
     */
    private BroadcastHandler broadcastHandler;

    /**
     * Listener for the receiving broadcasts.
     */
    private NetworkHandlerReceiver broadcastReceiver;

    /**
     * Map that stores routing table entries.
     * Each device number acts as a key,
     * Each key has a lists of TableEntry objects.
     */
    private Map<Integer, List<TableEntry>> table;


    /**
     * Inner class for a table entry.
     */
    class TableEntry {

        /**
         * This is the device to which there is a known route.
         */
        private int destination;

        /**
         * The next hop needed to reach the destination.
         */
        private int nextHop;

        /**
         * Total distance to the destination.
         */
        private int distance;


        /**
         * Constructorss
         * @param destination The destination device.
         * @param nextHop Next hop in route.
         * @param distance The distance to the destination.
         */
        public TableEntry(int destination, int nextHop, int distance) {
            this.destination = destination;
            this.nextHop = nextHop;
            this.distance = distance;
        }

        /**
         * Getter.
         * @return The destination.
         */
        public int getDestination() {
            return destination;
        }

        /**
         * Getter.
         * @return The next hop.
         */
        public int getNextHop() {
            return nextHop;
        }

        /**
         * Getter.
         * @return The distance to the destination.
         */
        public int getDistance() {
            return distance;
        }

        /**
         * Setter.
         * @param destination The new destination.
         */
        public void setDestination(int destination) {
            this.destination = destination;
        }

        /**
         * Setter.
         * @param nextHop The new next hop.
         */
        public void setNextHop(int nextHop) {
            this.nextHop = nextHop;
        }

        /**Setter.
         * @param distance The new distance.
         */
        public void setDistance(int distance) {
            this.distance = distance;
        }
    }


    /**
     * Constructor.
     * Starts the automatic broadcaster and the listener for incoming broadcasts.
     * @param client The client instantiating this object.
     */
    public RoutingTable(Client client) {
        this.table = new HashMap<>();
        this.client = client;
        //this.testTable();
          this.broadcastHandler = new BroadcastHandler(this);
          this.initializeTable();
          this.broadcastReceiver = new NetworkHandlerReceiver(this.client, this.client.getGroupSocket());

    }

    /*
    <x>.d.n.d/d.n.d/
     */

    /**
     * Method for converting the map representation of the table into a String for easier transmission over the network.
     * Each key is converted to '<key>'
     * After each key, the three values from the TableEntry object will follow as ",data1,data2,data3".
     * Each TableEntry object from the list is ended with a '/'.
     * Eg. if device number 1 has a TableEntry of <2, 3, 2> (it can reach device number 2 via 3, with a distance of 2),
     * the String representation will be <1>,2,3,2/
     * @param map The table to be sent.
     * @return The String representation.
     */
    public String convertToStringMessage(Map<Integer, List<TableEntry>> map) {
        String result = "";
        for(int i : map.keySet()) {
            result += "<" + i  + ">";
            for(TableEntry t : map.get(i)) {
                result += t.getDestination() + "," + t.getNextHop() + "," + t.getDistance() + "/";
            }
        }
        //System.out.println(result);
        return result;
    }


    public void testTable() {
        // list from device 3
        TableEntry tb = new TableEntry(1, 2, 2);
        TableEntry tb2 = new TableEntry(3, 3, 0);
        TableEntry tbx = new TableEntry(4, 4, 1);
        List<TableEntry> list = new ArrayList<>();
        list.add(tb);
        list.add(tb2);
        list.add(tbx);
        table.put(3, list);

        // list from device 2
        TableEntry tb3 = new TableEntry(2, 2, 0);
        TableEntry tb4 = new TableEntry(1, 1, 1);
        List<TableEntry> list2 = new ArrayList<>();
        list2.add(tb3);
        list2.add(tb4);
        table.put(2, list2);

        String parsed = this.convertToStringMessage(table);
        System.out.println(this.convertToStringMessage(table));
        System.out.println(this.convertToStringMessage(this.parseTable(parsed)));
    }

    /**
     * Method for creating a starting table.
     * It stores a single entry: <itself, itself, 0> (it can reach itself via itself with a distance of 0).
     * It parses the table and sets it as the default message of the broadcaster.
     */
    public void initializeTable() {
        TableEntry tb = new TableEntry(this.client.getDeviceNo(), this.client.getDeviceNo(), 0);
        List<TableEntry> list = new ArrayList<>();
        list.add(tb);
        table.put(this.client.getDeviceNo(), list);
        //System.out.println(this.convertToStringMessage(table));
        this.parseTable(this.convertToStringMessage(table));
        this.broadcastHandler.setMessage(this.convertToStringMessage(table));
        this.printTable();
    }

    /*
    <x.d.n.d/d.n.d/>
     */

    /**
     * Method for converting a String representation of a table into a map.
     * @param message The String representation of the table.
     * @return The map representation.
     */
    public Map<Integer, List<TableEntry>> parseTable (String message) {
        Map<Integer, List<TableEntry>> result = new HashMap<>();
        String[] devices = message.split("<");
        for(String s : devices) {
            if(s.isEmpty()){
                continue;
            }
            int deviceNo = s.charAt(0) - '0';

            List<TableEntry> list = new ArrayList<>();
            String[] contents = s.split("/");
            for(String c : contents){
                String[] entries = c.split(",");
                //System.out.println(entries);
                List<Integer> tableEntries = new ArrayList<>();
                for(String f : entries) {
                    tableEntries.add(f.charAt(f.length() - 1) - '0');
                }
                TableEntry tb = new TableEntry(tableEntries.get(0), tableEntries.get(1), tableEntries.get(2));
                list.add(tb);
            }
            result.put(deviceNo, list);
        }
        return result;
    }

    /**
     * Checks to see if we can add a new entry to our list or find an entry with a cheaper distance.
     * If we find such an entry, update the list.
     * @param entry The entry used to compare.
     * @param list The list in which it will search.
     * @return True if there was an update, false if not.
     */
    public boolean compareEntry(TableEntry entry, List<TableEntry> list) {

        boolean found = false;
        for (TableEntry tb : list) {
            if (tb.getDestination() == entry.getDestination()) {
                found = true;
                if (tb.getDistance() > entry.getDistance() && entry.getDistance() != 0) {
                    //System.out.println("Removed: " + tb.getDistance() + " " + tb.getDestination() + " " + tb.getNextHop());
                    list.remove(tb);
                    //System.out.println("Added: " + entry.getDistance() + " " + entry.getDestination() + " " + entry.getNextHop());
                    list.add(entry);
                    return true;
                }
            }
            if (!found) {
                //System.out.println("Added: " + entry.getDistance() + " " + entry.getDestination() + " " + entry.getNextHop() + "(not found)");
                list.add(entry);
                return true;
            }
        }
        return false;
    }

    /**
     * Method for receiving a packet from the Processing Layer.
     * Parses the messages and tries to update the table.
     * @param packet The packet received.
     */
    public void receiveFromProcessingLayer(Packet packet) {

        Map<Integer, List<TableEntry>> receivedTable = parseTable(packet.getMessage());

        boolean updateTable = false;

        // Iterate through each key in the received table
        for(int i : receivedTable.keySet()) {
            // Check to see if there is a list with the given key in our table
            List<TableEntry> list = this.table.get(i);
            if(list == null) {
                // If not, we add it.
                List<TableEntry> newList = receivedTable.get(i);
                for(TableEntry t : newList) {
                    t.setDistance(t.getDistance() + 1);
                }
                this.table.put(i, newList);
                updateTable = true;
            } else {
                // There is already a list with the given key in our table.
                List<TableEntry> receivedList = receivedTable.get(i);
                // Go through each entry in the received table and check if there is a similar entry in our table.
                for(TableEntry entry : receivedList) {
                    if (compareEntry(entry, list)) {
                        updateTable = true;
                    }
                }
            }
        }

        if(updateTable) {
            this.printTable();
            this.broadcastHandler.setMessage(this.convertToStringMessage(table));
        }
    }

    /**
     * Method for printing a visual representation of the table.
     */
    public void printTable() {
        System.out.println("-------------------START---------------------");
        for(int i : this.table.keySet()) {
            System.out.println("Entries for device number: " + i);
            for(TableEntry t : this.table.get(i)) {
                System.out.println("Destination : " + t.getDestination());
                System.out.println("Nexthop: " + t.getNextHop());
                System.out.println("Distance: " + t.getDistance());
            }
        }

        System.out.println("-------------------END---------------------");
    }

    /**
     * Getter.
     * @return The client that instantiated this object.
     */
    public Client getClient() {
        return this.client;
    }

}
