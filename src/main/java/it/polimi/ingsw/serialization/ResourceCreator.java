package it.polimi.ingsw.serialization;

import com.google.gson.*;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;

import java.lang.reflect.Type;

public class ResourceCreator implements JsonDeserializer<Resource> {

    @Override
    public Resource deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Resource resource;
        if (json.getAsString().equals("FAITH_POINT") || json.getAsString().equals("VOID")) {
            resource = NonPhysicalResourceType.valueOf(json.getAsString());
        } else
            resource = ResourceType.valueOf(json.getAsString());
        return resource;
    }
}
