package it.polimi.ingsw.serialization;

import com.google.gson.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.ResourceType;

import java.lang.reflect.Type;

public class LeaderCardCreator implements JsonDeserializer<LeaderCard> {
    @Override
    public LeaderCard deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        LeaderCard leaderCard;

        if (jsonObject.has("resourceType"))
            leaderCard = new DepotLeaderCard(
                    jsonObject.get("victoryPoints").getAsInt(),
                    new RequirementsCreator().deserialize(jsonObject.get("requirements"), Requirements.class, context),
                    ResourceType.valueOf(jsonObject.get("resourceType").getAsString())
            );

        else if (jsonObject.has("discountResourceType"))
            leaderCard = new DiscountLeaderCard(
                    jsonObject.get("victoryPoints").getAsInt(),
                    new RequirementsCreator().deserialize(jsonObject.get("requirements"), Requirements.class, context),
                    ResourceType.valueOf(jsonObject.get("discountResourceType").getAsString()),
                    jsonObject.get("discount").getAsInt()
            );

        else if (jsonObject.has("productionCost"))
            leaderCard = new ProductionLeaderCard(
                    jsonObject.get("victoryPoints").getAsInt(),
                    new RequirementsCreator().deserialize(jsonObject.get("requirements"), Requirements.class, context),
                    new RequirementsCreator().deserialize(jsonObject.get("requirements"), Requirements.class, context)
                    );

        else
            leaderCard = new WhiteMarbleLeaderCard(
                    jsonObject.get("victoryPoints").getAsInt(),
                    new RequirementsCreator().deserialize(jsonObject.get("requirements"), Requirements.class, context),
                    ResourceType.valueOf(jsonObject.get("outputResourceType").getAsString())
            );


        return leaderCard;
    }
}
