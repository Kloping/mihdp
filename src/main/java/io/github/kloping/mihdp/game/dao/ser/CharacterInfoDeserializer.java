package io.github.kloping.mihdp.game.dao.ser;

import com.google.gson.*;
import io.github.kloping.mihdp.game.dao.Attr;
import io.github.kloping.mihdp.game.dao.BaseCharacterInfo;
import io.github.kloping.mihdp.game.dao.CharacterInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class CharacterInfoDeserializer implements JsonDeserializer<CharacterInfo> {
    @Override
    public CharacterInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CharacterInfo info = new CharacterInfo();
        JsonObject jsonObject = json.getAsJsonObject();
        for (String s : jsonObject.keySet()) {
            try {
                JsonElement element = jsonObject.get(s);
                if (element == null) continue;
                Field field = null;
                try {
                    field = CharacterInfo.class.getDeclaredField(s);
                } catch (NoSuchFieldException e) {
                    field = BaseCharacterInfo.class.getDeclaredField(s);
                }
                field.setAccessible(true);
                if (field.getType() == Attr.class) {
                    Attr attr = new Attr(s, element.getAsInt());
                    field.set(info, attr);
                } else if (field.getType() == String.class) {
                    field.set(info, element.getAsString());
                } else if (field.getType() == Integer.class) {
                    field.set(info, element.getAsInt());
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return info;
    }
}