import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.View.CLI.CLIController;

public class App
{
    public static void main( String[] args )
    {
        GameController gameController = new GameController();

        CLIController cliController = new CLIController(gameController);
        cliController.start();
    }
}
