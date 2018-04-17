package ApplicationLayer;

import ProcessingLayer.Packet;
import TransportLayer.NetworkHandlerReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutingTable {

    private Client client;
    private BroadcastHandler broadcastHandler;
    private NetworkHandlerReceiver broadcastReceiver;
    private Map<Integer, List<TableEntry>> table;


    private class TableEntry {

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
        this.client = client;
        this.broadcastHandler = new BroadcastHandler(this.client);
        this.broadcastReceiver = new NetworkHandlerReceiver(this.client, this.client.getGroupSocket());
        this.table = new HashMap<>();
    }

    public void receiveFromProcessingLayer(Packet packet) {
        int deviceNo = packet.getSourcePort() % 10;
        TableEntry entry = new TableEntry(deviceNo, packet.getNextHop(), 1);
        List<TableEntry>  list = table.get(deviceNo);

        if(list == null) {
            table.put(deviceNo, list = new ArrayList<>());
            list.add(entry);
            this.printTable();
        } else {
            for(TableEntry i : list) {
              if(i.getDestination() == entry.getDestination() && i.getDistance() > entry.getDistance()) {
                  list.remove(i);
                  list.add(entry);
                  this.printTable();
              }
            }
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

}
