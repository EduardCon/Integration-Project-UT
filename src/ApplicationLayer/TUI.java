package ApplicationLayer;

import java.util.concurrent.TimeUnit;

public class TUI {

    private static Client client;

    public static void main(String[] args) throws Exception {
        client=new Client("Eduard C ", 54322);
        client.connect();

//        Encryption encryption = new Encryption();
//        String message = "test";
//        String criptat = encryption.encrypt(message);
//        System.out.println(criptat.length());
//        String decriptat = encryption.decrypt(criptat);
//        System.out.println(decriptat);

       // while(true){
         //   client.sendToProceessingLayer("ediC", 54322);
           // TimeUnit.SECONDS.sleep(2);
        //}
        //client.sendToProceessingLayer("Test 2", 54323);

    }
}
