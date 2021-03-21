import it.polimi.ingsw.Utils.InterfaceAdapter;
import it.polimi.ingsw.Utils.Pair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.Model.*;


public class App 
{
    public static void main( String[] args )
    {

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Resource.class, new InterfaceAdapter());
        Gson gson = builder.create();

        Gson gson2 = new Gson();

        DevelopmentCard app = new DevelopmentCard(6,new Requirements(new Pair<>(ResourceType.SHIELD,3),new Pair<>(ResourceType.SERVANT,2)),2, DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.SHIELD,1)),new Pair<>(ResourceType.SERVANT,1));
        String ris = gson.toJson(app);
        System.out.println(ris);
        System.out.println(gson2.toJson(app));
        DevelopmentCard r = gson.fromJson(ris,DevelopmentCard.class);
        System.out.println( gson.toJson(r.getProductionCost()) );
    }
}
