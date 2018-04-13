package ApplicationLayer;

import java.io.IOException;

public class TUI {

    private static Client client;

    public static void main(String[] args) throws IOException {
        client=new Client("Eduard");
        client.connect();
        client.sendToProceessingLayer("Doamne ajuta!", 54322);
    }
}
