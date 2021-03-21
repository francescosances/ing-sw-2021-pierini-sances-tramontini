package Utils;

import com.google.gson.JsonObject;

public interface Serializable<T> {

    public JsonObject toJSON();

    public T fromJSON(String jsonObject);

}
