package it.polimi.ingsw.Utils;

import com.google.gson.*;
import it.polimi.ingsw.Model.ResourceType;

import java.lang.reflect.Type;

//https://technology.finra.org/code/serialize-deserialize-interfaces-in-java.html

public class InterfaceAdapter<T> implements JsonDeserializer<T> {

    public T deserialize(JsonElement jsonElement, Type type,
                         JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Class klass = ResourceType.class;
        return jsonDeserializationContext.deserialize(jsonElement, klass);
    }
}
