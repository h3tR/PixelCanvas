package ml.jozefpeeterslaan72wuustwezel.json;

import com.google.gson.JsonArray;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class JsonHelpers {

    public static JsonArray Vector2fToJson(Vector2f vec){
        JsonArray json = new JsonArray();
        json.add(vec.x);
        json.add(vec.y);
        return json;
    }
    public static JsonArray Vector4fToJson(Vector4f vec){
        JsonArray json = new JsonArray();
        json.add(vec.x);
        json.add(vec.y);
        json.add(vec.z);
        json.add(vec.w);
        return json;
    }
    public static Vector2f Vector2fFromJson(JsonArray json){
        return new Vector2f(json.get(0).getAsFloat(),json.get(1).getAsFloat());
    }
    public static Vector4f Vector4fFromJson(JsonArray json){
        return new Vector4f(json.get(0).getAsFloat(),json.get(1).getAsFloat(),json.get(2).getAsFloat(),json.get(3).getAsFloat());
    }
}
