package it.polimi.ingsw.serialization;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.cards.DevelopmentColorType;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.cards.Requirements;
import it.polimi.ingsw.model.storage.ResourceType;

public class RequirementsCreator implements JsonDeserializer<Requirements> {

    @Override
    public Requirements deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Requirements requirements = new Requirements();
        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("resources")) {
            JsonObject jsonObjectMap = jsonObject.get("resources").getAsJsonObject();
            for (String stringResource : jsonObjectMap.keySet()) {
                if (stringResource.equals("FAITH_POINT") || stringResource.equals("ON_DEMAND"))
                    requirements.addResourceRequirement(NonPhysicalResourceType.valueOf(stringResource), jsonObjectMap.get(stringResource).getAsInt());
                else
                    requirements.addResourceRequirement(ResourceType.valueOf(stringResource), jsonObjectMap.get(stringResource).getAsInt());
            }
        }

        if (jsonObject.has("developmentCards")) {
            JsonObject jsonObjectMap = jsonObject.get("developmentCards").getAsJsonObject();
            for (String stringColor : jsonObjectMap.keySet()) {

                JsonElement jsonInnerMap = jsonObjectMap.get(stringColor);
                Type mapType = new TypeToken<Map<Integer, Integer>>() {}.getType();
                Map<Integer,Integer> innerMap = context.deserialize(jsonInnerMap, mapType);

                for (int i:innerMap.keySet()) {
                    requirements.addDevelopmentCardRequirement(
                            DevelopmentColorType.valueOf(stringColor),
                            i,
                            innerMap.get(i));
                }
            }
        }
        return requirements;
    }

}

