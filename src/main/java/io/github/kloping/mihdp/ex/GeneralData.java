package io.github.kloping.mihdp.ex;

import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import io.github.kloping.judge.Judge;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

@Data
public class GeneralData {
    public static final Type TYPE_TOKEN = new TypeToken<GeneralData>() {
    }.getType();

    public <T extends GeneralData> T find(Class<T> cla) {
        return cla == this.getClass() ? (T) this : null;
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

        public ResDataImage(String data) {
            this.type = "image";
            this.data = data;
        }

        public ResDataImage(byte[] data) {
            this.type = "image";
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

        public void filterAt(String botId) {
            Iterator<GeneralData> iterator = list.listIterator();
            while (iterator.hasNext()) {
                GeneralData data = iterator.next();
                if (data instanceof GeneralData.ResDataAt) {
                    GeneralData.ResDataAt at = (ResDataAt) data;
                    if (at.getId().equals(botId))
                        iterator.remove();
                }
            }
        }
    }
}
