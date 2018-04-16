package ApplicationLayer;

import TransportLayer.NetworkHandlerReceiver;

public class RoutingTable {

    private Client client;
    private BroadcastHandler broadcastHandler;
    private NetworkHandlerReceiver broadcastReceiver;


    public RoutingTable(Client client) {
        this.client = client;
        this.broadcastHandler = new BroadcastHandler(this.client);
        this.broadcastReceiver = new NetworkHandlerReceiver(this.client, this.client.getGroupSocket());
    }

}
