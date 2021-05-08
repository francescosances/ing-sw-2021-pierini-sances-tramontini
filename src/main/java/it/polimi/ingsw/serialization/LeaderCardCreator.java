package it.polimi.ingsw.serialization;

import com.google.gson.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;

import java.lang.reflect.Type;

public class LeaderCardCreator implements JsonDeserializer<LeaderCard> {
    @Override
    public LeaderCard deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        LeaderCard leaderCard;

        if (jsonObject.has("resourceType"))
            leaderCard = depotLeaderCard(jsonObject, context);

        else if (jsonObject.has("discountResourceType"))
            leaderCard = discountLeaderCard(jsonObject, context);

        else if (jsonObject.has("productionCost"))
            leaderCard = productionLeaderCard(jsonObject, context);

        else
            leaderCard = whiteMarbleLeaderCard(jsonObject, context);

        return leaderCard;
    }

    protected DepotLeaderCard depotLeaderCard(JsonObject jsonObject, JsonDeserializationContext context) {
        DepotLeaderCard leaderCard = new DepotLeaderCard(
                jsonObject.get("cardName").getAsString(),
                jsonObject.get("victoryPoints").getAsInt(),
                new RequirementsCreator().deserialize(jsonObject.get("requirements"), Requirements.class, context),
                ResourceType.valueOf(jsonObject.get("resourceType").getAsString()),
                jsonObject.get("active").getAsBoolean()
        );
        for (int i = 0; i < jsonObject.get("occupied").getAsInt(); i++) {
            try {
                leaderCard.addResource(ResourceType.valueOf(jsonObject.get("resourceType").getAsString()));
            } catch (IncompatibleDepotException e) {
                e.printStackTrace();
            }
        }
        return leaderCard;
    }

    protected DiscountLeaderCard discountLeaderCard(JsonObject jsonObject, JsonDeserializationContext context) {

        return new DiscountLeaderCard(
                jsonObject.get("cardName").getAsString(),
                jsonObject.get("victoryPoints").getAsInt(),
                new RequirementsCreator().deserialize(jsonObject.get("requirements"), Requirements.class, context),
                ResourceType.valueOf(jsonObject.get("discountResourceType").getAsString()),
                jsonObject.get("discount").getAsInt(),
                jsonObject.get("active").getAsBoolean()
        );
    }

    protected static ProductionLeaderCard productionLeaderCard(JsonObject jsonObject, JsonDeserializationContext context) {
        return new ProductionLeaderCard(
                jsonObject.get("cardName").getAsString(),
                jsonObject.get("victoryPoints").getAsInt(),
                new RequirementsCreator().deserialize(jsonObject.get("requirements"), Requirements.class, context),
                new RequirementsCreator().deserialize(jsonObject.get("productionCost"), Requirements.class, context),
                jsonObject.get("active").getAsBoolean()
        );
    }

    protected WhiteMarbleLeaderCard whiteMarbleLeaderCard(JsonObject jsonObject, JsonDeserializationContext context) {
        return new WhiteMarbleLeaderCard(
                jsonObject.get("cardName").getAsString(),
                jsonObject.get("victoryPoints").getAsInt(),
                new RequirementsCreator().deserialize(jsonObject.get("requirements"), Requirements.class, context),
                ResourceType.valueOf(jsonObject.get("outputResourceType").getAsString()),
                jsonObject.get("active").getAsBoolean()
        );
    }
}