package it.polimi.ingsw.serialization;

import com.google.gson.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.Depot;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.model.storage.StandardDepot;

import java.lang.reflect.Type;

public class DepotCreator implements JsonDeserializer<Depot> {
    @Override
    public Depot deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Depot depot;

        if (jsonObject.has("victoryPoints")) {
            depot = new DepotLeaderCard(
                    jsonObject.get("victoryPoints").getAsInt(),
                    new RequirementsCreator().deserialize(jsonObject.get("requirements"), Requirements.class, context),
                    ResourceType.valueOf(jsonObject.get("resourceType").getAsString())
            );
            DepotLeaderCard depotLeaderCard = (DepotLeaderCard) depot;
            for (int i = 0; i < jsonObject.get("occupied").getAsInt(); i++) {
                depotLeaderCard.addResource(ResourceType.valueOf(jsonObject.get("resourceType").getAsString()));
            }
        }

        else {
            depot = new StandardDepot(jsonObject.get("size").getAsInt());
            for (int i = 0; i < jsonObject.get("occupied").getAsInt(); i++)
            depot.addResource(ResourceType.valueOf(jsonObject.get("resourceType").getAsString()));
        }

        return depot;
    }
}

