package wang.depp.session.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String getJson(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        String result = gsonBuilder.setPrettyPrinting().create().toJson(object);
        return result;
    }

    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.setPrettyPrinting().create().fromJson(json, classOfT);
    }

    public static String toString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
