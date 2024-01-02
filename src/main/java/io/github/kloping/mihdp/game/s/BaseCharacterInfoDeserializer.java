package io.github.kloping.mihdp.game.s;

import com.google.gson.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class BaseCharacterInfoDeserializer implements JsonDeserializer<BaseCharacterInfo> {
    @Override
    public BaseCharacterInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        BaseCharacterInfo info = new BaseCharacterInfo();
        JsonObject jsonObject = json.getAsJsonObject();
        for (String s : jsonObject.keySet()) {
            JsonElement element = jsonObject.get(s);
            if (element == null) continue;
            int v = element.getAsInt();
            Attr attr = new Attr(s, v);
            try {
                Field field = BaseCharacterInfo.class.getDeclaredField(s);
                field.setAccessible(true);
                field.set(info, attr);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return info;
    }
}