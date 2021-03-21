import Utils.Pair;
import com.google.gson.Gson;
import it.polimi.ingsw.Model.DevelopmentCard;
import it.polimi.ingsw.Model.DevelopmentColorType;
import it.polimi.ingsw.Model.Requirements;
import it.polimi.ingsw.Model.ResourceType;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        Gson gson = new Gson();

        DevelopmentCard app = new DevelopmentCard(6,new Requirements(new Pair<>(ResourceType.SHIELD,3),new Pair<>(ResourceType.SERVANT,2)),2, DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.SHIELD,1)),new Pair<>(ResourceType.SERVANT,1));
        String ris = gson.toJson(app);
        System.out.println(ris);
        DevelopmentCard r = gson.fromJson(ris,DevelopmentCard.class);
        System.out.println( r.getLevel() );
    }
}
