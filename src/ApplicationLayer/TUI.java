package ApplicationLayer;

import java.io.IOException;

public class TUI {

    private static Client client;

    public static void main(String[] args) throws IOException {
        client=new Client("Eduard C");
        client.connect();
        client.sendToProceessingLayer("CRISTIANO RONALDO", 54322);
    }
}
