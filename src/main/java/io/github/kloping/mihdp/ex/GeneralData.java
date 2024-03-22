package io.github.kloping.mihdp.ex;

import com.alibaba.fastjson.JSON;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.github.kloping.judge.Judge;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

@Data
public class GeneralData {
    public static final Type TYPE_TOKEN = new TypeToken<GeneralData>() {
    }.getType();

    public <T extends GeneralData> T find(Class<T> cla) {
        return cla == this.getClass() ? (T) this : null;
    }

    public String allText() {
        return "";
    }

    protected String type;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ResDataText extends GeneralData {
        private String content;

        public ResDataText(String text) {
            this.type = "text";
            this.content = text;
        }

        @Override
        public <T extends GeneralData> T find(Class<T> cla) {
            return (Judge.isNotEmpty(content) && cla == this.getClass()) ? (T) this : null;
        }

        @Override
        public String allText() {
            return content;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ResDataSelect extends GeneralData {
        private Integer s;
        private String content = "";

        public ResDataSelect(Integer s) {
            this.type = "select";
            this.s = s;
        }

        public ResDataSelect(Integer s, String content) {
            this(s);
            this.content = content;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ResDataButton extends GeneralData {
        /**
         * 显示内容
         */
        private String text = "按钮";
        /**
         * 数据内容
         */
        private String content = "默认数据";

        public ResDataButton(String text, String content) {
            this.type = "button";
            this.text = text;
            this.content = content;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ResDataAt extends GeneralData {
        private String id;

        public ResDataAt(String id) {
            this.type = "at";
            this.id = id;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ResDataImage extends GeneralData {
        //base64
        private String data;
        private String p = "base64";
        private Integer w;
        private Integer h;

        public ResDataImage(String data, int w, int h) {
            this.type = "image";
            this.w = w;
            this.h = h;
            this.data = data;
        }

        public ResDataImage(String data, String p, int w, int h) {
            this.type = "image";
            this.w = w;
            this.h = h;
            this.p = p;
            this.data = data;
        }

        public ResDataImage(byte[] data, int w, int h) {
            this.type = "image";
            this.w = w;
            this.h = h;
            this.data = Base64.getEncoder().encodeToString(data);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ResDataChain extends GeneralData {
        private List<GeneralData> list;

        public ResDataChain(List<GeneralData> list) {
            this.list = list;
            this.type = "chain";
        }

        @Override
        public <T extends GeneralData> T find(Class<T> cla) {
            GeneralData data = super.find(cla);
            if (data == null) {
                for (GeneralData generalData : list) {
                    if (generalData.find(cla) != null) return (T) generalData;
                }
            }
            return (T) data;
        }

        public List<GeneralData> filterAt(String botId) {
            List<GeneralData> d0 = new LinkedList<>();
            for (GeneralData data : list) {
                if (data instanceof ResDataAt) {
                    ResDataAt at = (ResDataAt) data;
                    if (at.getId().equals(botId)) continue;
                }
                d0.add(data);
            }
            return d0;
        }

        @Override
        public String allText() {
            StringBuilder sb = new StringBuilder();
            for (GeneralData data : list) {
                sb.append(data.allText());
            }
            return sb.toString();
        }
    }

    public static class GeneralDataBuilder {
        private List<GeneralData> list = new LinkedList<>();

        public static GeneralDataBuilder create(String text) {
            return new GeneralDataBuilder().append(new ResDataText(text));
        }

        public GeneralDataBuilder append(GeneralData data) {
            list.add(data);
            return this;
        }

        public GeneralDataBuilder append(String text) {
            return append(new ResDataText(text));
        }

        public GeneralDataBuilder append(Integer s, String text) {
            return append(new ResDataSelect(s, text));
        }

        public ResDataChain build() {
            return new ResDataChain(list);
        }
    }

    public static class GeneralDataDeserializer implements JsonDeserializer<GeneralData> {
        @Override
        public GeneralData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            GeneralData data = null;
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement name = jsonObject.get("type");
            switch (name.getAsString()) {
                case "text":
                    data = new ResDataText(jsonObject.get("content").getAsString());
                    break;
                case "image":
                    String base64 = jsonObject.get("data").getAsString();
                    data = new ResDataImage(base64, jsonObject.get("w").getAsInt(), jsonObject.get("h").getAsInt());
                    if (base64.startsWith("http")) ((ResDataImage) data).setP("http");
                    break;
                case "at":
                    data = new ResDataAt(jsonObject.get("id").getAsString());
                    break;
                case "chain":
                    List<GeneralData> list = new LinkedList<>();
                    for (JsonElement jsonElement : jsonObject.get("list").getAsJsonArray()) {
                        GeneralData d0 = deserialize(jsonElement, typeOfT, context);
                        list.add(d0);
                    }
                    data = new ResDataChain(list);
                    break;
                case "select":
                    data = new ResDataSelect(jsonObject.get("s").getAsInt(), jsonObject.get("content").getAsString());
                    break;
                case "button":
                    data = new ResDataButton(jsonObject.get("text").getAsString(), jsonObject.get("content").getAsString());
                    break;
                default:
                    System.err.println("unknown type");
                    break;
            }
            return data;
        }
    }
}
