//package Tests;
//
//import ApplicationLayer.Client;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class ClientTest {
//    private Client client;
//
//    @Before
//    public void setUp() {
//        this.client = new Client("Test", 9001);
//    }
//
//    @Test
//    public void testCreateClient() {
//        assertTrue(this.client.getName() == "Test");
//        assertTrue(this.client.getDeviceNo() == 1);
//        assertTrue(this.client.getListeningPort() == 9001);
//    }
//
//    @Test
//    public void testConnection() {
//        this.client.connect();
//        assertTrue(!this.client.getReceivedBuffer().isEmpty());
//        assertTrue(this.client.getSocket().isConnected());
//        assertTrue(this.client.getGroupSocket().isConnected());
//        assertTrue(this.client.getSender() != null);
//        assertTrue(this.client.getRoutingTable() != null);
//    }
//
//    @Test
//    public void testSendReceiveMessage() throws Exception {
//        String message = "This is a test message";
//        this.client.connect();
//        this.client.sendToProceessingLayer(message, 4464);
//        String receivedMessage = this.client.lastMessageTodDisplay;
//        System.out.println(receivedMessage);
//        assertTrue(message == receivedMessage);
//    }
//
//}