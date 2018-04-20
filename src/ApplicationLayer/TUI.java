package ApplicationLayer;

import EncryptionLayer.Encryption;

import java.util.concurrent.TimeUnit;

public class TUI {

    private static Client client;

    public static void main(String[] args) throws Exception {
        client=new Client("Eduard C ", 54322);
        client.connect();

//        String test = "";
//        for(int i = 0; i < 10; i++) {
//            test += "1";
//        }

        String test = "Hello world";

        while(true) {
            client.sendToProceessingLayer(test, 4464);
            TimeUnit.SECONDS.sleep(2);
        }



       // while(true){
         //   client.sendToProceessingLayer("ediC", 54322);
           // TimeUnit.SECONDS.sleep(2);
        //}
        //client.sendToProceessingLayer("Test 2", 54323);

    }
}
