package it.polimi.ingsw.network;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.DevelopmentColorType;
import it.polimi.ingsw.model.NonPhysicalResourceType;
import it.polimi.ingsw.model.Requirements;
import it.polimi.ingsw.model.ResourceType;

public class RequirementsCreator implements JsonDeserializer<Requirements> {

    @Override
    public Requirements deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Requirements requirements = new Requirements();
        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("resources")) {
            JsonObject jsonObjectMap = jsonObject.get("resources").getAsJsonObject();
            for (String stringResource : jsonObjectMap.keySet()) {
                if (stringResource.equals("FAITH_POINT")) {
                    requirements.addResourceRequirement(NonPhysicalResourceType.FAITH_POINT, jsonObjectMap.get(stringResource).getAsInt());
                } else
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

