package io.github.kloping.mihdp.ex;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class DataDeserializer implements JsonDeserializer<GeneralData> {

    @Override
    public GeneralData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        GeneralData data = null;
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement name = jsonObject.get("type");
        switch (name.getAsString()) {
            case "text":
                data = new GeneralData.ResDataText(jsonObject.get("content").getAsString());
                break;
            case "image":
                String base64 = jsonObject.get("data").getAsString();
                data = new GeneralData.ResDataImage(base64);
                if (base64.startsWith("http"))
                    ((GeneralData.ResDataImage) data).setP("http");
                break;
            case "at":
                data = new GeneralData.ResDataAt(jsonObject.get("id").getAsString());
                break;
            case "chain":
                List<GeneralData> list = new LinkedList<>();
                for (JsonElement jsonElement : jsonObject.get("list").getAsJsonArray()) {
                    GeneralData d0 = deserialize(jsonElement, typeOfT, context);
                    list.add(d0);
                }
                data = new GeneralData.ResDataChain(list);
                break;
        }
        return data;
    }
}