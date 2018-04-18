package ApplicationLayer;

import ProcessingLayer.Packet;
import TransportLayer.NetworkHandlerReceiver;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.scene.control.Tab;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutingTable {

    private Client client;
    private BroadcastHandler broadcastHandler;
    private NetworkHandlerReceiver broadcastReceiver;
    private Map<Integer, List<TableEntry>> table;


    class TableEntry {

        private int destination;
        private int nextHop;
        private int distance;


        public TableEntry(int destination, int nextHop, int distance) {
            this.destination = destination;
            this.nextHop = nextHop;
            this.distance = distance;
        }

        public int getDestination() {
            return destination;
        }

        public int getNextHop() {
            return nextHop;
        }

        public int getDistance() {
            return distance;
        }
    }


    public RoutingTable(Client client) {
        this.table = new HashMap<>();
        this.client = client;
        this.testTable();
//        this.broadcastHandler = new BroadcastHandler(this);
//        this.initializeTable();
//        this.broadcastReceiver = new NetworkHandlerReceiver(this.client, this.client.getGroupSocket());

    }

    /*
    <x>.d.n.d/d.n.d/
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

    public void initializeTable() {
        TableEntry tb = new TableEntry(this.client.getDeviceNo(), this.client.getDeviceNo(), 0);
        List<TableEntry> list = new ArrayList<>();
        list.add(tb);
        table.put(this.client.getDeviceNo(), list);
        System.out.println(this.convertToStringMessage(table));
        this.parseTable(this.convertToStringMessage(table));
        this.broadcastHandler.setMessage(this.convertToStringMessage(table));

    }

    /*
    <x.d.n.d/d.n.d/>
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

    public boolean compareEntry(TableEntry entry, List<TableEntry> list) {
        for(TableEntry tb : list) {
            if(tb.getDestination() == entry.getDestination() && tb.getDistance() > entry.getDistance()) {
                list.remove(tb);
                list.add(entry);
                return true;
            }
        }
        return false;
    }

    public void receiveFromProcessingLayer(Packet packet) {

        Map<Integer, List<TableEntry>> receivedTable = parseTable(packet.getMessage());

        boolean updateTable = false;

        // Iterate through each key in the received table
        for(int i : receivedTable.keySet()) {
            // Check to see if there is a list with the given key in our table
            List<TableEntry> list = this.table.get(i);
            if(list == null) {
                // If not, we add it.
                this.table.put(i, list);
                updateTable = true;
            } else {
                // There is already a list with the given key in our table.
                List<TableEntry> receivedList = receivedTable.get(i);
                // Go through each entry in the received table and check if there is a similar entry in our table.
                for(TableEntry entry : receivedList) {
                    if(compareEntry(entry, list)) {
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

    public Client getClient() {
        return this.client;
    }

}
