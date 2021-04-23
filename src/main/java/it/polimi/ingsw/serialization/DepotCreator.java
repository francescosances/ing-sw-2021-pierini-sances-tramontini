package it.polimi.ingsw.serialization;

import com.google.gson.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.Depot;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.model.storage.StandardDepot;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;

import java.lang.reflect.Type;

public class DepotCreator implements JsonDeserializer<Depot> {
    @Override
    public Depot deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Depot depot;

        if (jsonObject.has("victoryPoints"))
            depot = new LeaderCardCreator().depotLeaderCard(jsonObject, context);

        else
            depot = standardDepot(jsonObject);

        return depot;
    }

    protected StandardDepot standardDepot(JsonObject jsonObject){
        StandardDepot depot = new StandardDepot(jsonObject.get("size").getAsInt());
        for (int i = 0; i < jsonObject.get("occupied").getAsInt(); i++) {
            try {
                depot.addResource(ResourceType.valueOf(jsonObject.get("resourceType").getAsString()));
            } catch (IncompatibleDepotException e) {
                e.printStackTrace();
            }
        }
        return depot;
    }
}

