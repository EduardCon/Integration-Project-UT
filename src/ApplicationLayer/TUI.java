package ApplicationLayer;

public class TUI {

    private static Client client;

    public static void main(String[] args) throws Exception {
        client=new Client("Eduard C ", 54321);
        client.connect();

//        Encryption encryption = new Encryption();
//        String message = "test";
//        String criptat = encryption.encrypt(message);
//        System.out.println(criptat.length());
//        String decriptat = encryption.decrypt(criptat);
//        System.out.println(decriptat);

        client.sendToProceessingLayer("Test", 4464);
        //client.sendToProceessingLayer("Test 2", 54323);

    }
}
