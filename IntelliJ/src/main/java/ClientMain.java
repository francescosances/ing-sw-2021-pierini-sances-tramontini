import it.polimi.ingsw.network.Client;


public class ClientMain
{
    public static void main( String[] args )
    {
        // client, clientController and cli are created
        Client client = new Client();
        // start view and show welcome screen
        client.startCli(); // or client.startGui();

    }
}
