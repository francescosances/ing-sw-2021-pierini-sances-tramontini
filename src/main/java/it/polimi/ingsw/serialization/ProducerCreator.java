package it.polimi.ingsw.serialization;

import com.google.gson.*;
import it.polimi.ingsw.model.Producer;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentColorType;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.Requirements;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.ResourceType;

import java.lang.reflect.Type;

public class ProducerCreator implements JsonDeserializer<Producer> {
    @Override
    public Producer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Producer producer;
        if (jsonObject.has("productionCost"))
            producer = LeaderCardCreator.productionLeaderCard(jsonObject, context);
        else
            producer = developmentCard(jsonObject, context);
        return producer;
    }

    protected Producer developmentCard(JsonObject jsonObject, JsonDeserializationContext context) {
        return new DevelopmentCard(
                jsonObject.get("victoryPoints").getAsInt(),
                new RequirementsCreator().deserialize(jsonObject.get("cost"), Requirements.class, context),
                jsonObject.get("level").getAsInt(),
                DevelopmentColorType.valueOf(jsonObject.get("color").getAsString()),
                new RequirementsCreator().deserialize(jsonObject.get("productionCost"), Requirements.class, context),
                new RequirementsCreator().deserialize(jsonObject.get("productionGain"), Requirements.class, context)
                );
    }
}
