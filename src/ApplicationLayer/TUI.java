package ApplicationLayer;

import java.io.IOException;

public class TUI {

    private static Client client;

    public static void main(String[] args) throws IOException {
        client=new Client("Eduard M ");
        client.connect();

        client.sendToProceessingLayer("CRISTIANO RONALDO", 54322);
        client.sendToProceessingLayer("HAT TRICK", 54322);

    }
}
