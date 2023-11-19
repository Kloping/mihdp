package io.github.kloping.mihdp.wss.data;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class DataDeserializer implements JsonDeserializer<ResData> {

    /*
            School school = new School();

            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement name = jsonObject.get("name");
            school.setName(name.getAsString());

            List<People> peopleList = new ArrayList<>();
            school.setPeopleList(peopleList);

            JsonArray jsonArray = json.getAsJsonObject().get("peopleList").getAsJsonArray();
            for(JsonElement jsonElement : jsonArray) {

                JsonObject asJsonObject = jsonElement.getAsJsonObject();
                String stuName = asJsonObject.get("name").getAsString();
                Long cardId = asJsonObject.get("cardId").getAsLong();
                int age = asJsonObject.get("age").getAsInt();

                Student student = new Student(stuName, age, cardId);
                peopleList.add(student);
            }

            return school;
       */
    @Override
    public ResData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ResData data = null;
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement name = jsonObject.get("type");
        switch (name.getAsString()) {
            case "text":
                data = new ResData.ResDataText(jsonObject.get("content").getAsString());
                break;
            case "image":
                String base64 = jsonObject.get("data").getAsString();
                data = new ResData.ResDataImage(base64);
                if (base64.startsWith("http"))
                    ((ResData.ResDataImage) data).setP("http");
                break;
            case "at":
                data = new ResData.ResDataAt(jsonObject.get("id").getAsString());
                break;
            case "chain":
                List<ResData> list = new LinkedList<>();
                for (JsonElement jsonElement : jsonObject.get("list").getAsJsonArray()) {
                    ResData d0 = deserialize(jsonElement, typeOfT, context);
                    list.add(d0);
                }
                data = new ResData.ResDataChain(list);
                break;
        }
        return data;
    }
}