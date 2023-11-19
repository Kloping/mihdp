package io.github.kloping.mihdp.wss.data;

import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.List;

@Data
public class ResData {
    public static final Type TYPE_TOKEN = new TypeToken<ResData>() {
    }.getType();

    protected String type;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ResDataText extends ResData {
        private String content;

        public ResDataText(String text) {
            this.type = "text";
            this.content = text;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ResDataAt extends ResData {
        private String id;

        public ResDataAt(String id) {
            this.type = "at";
            this.id = id;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ResDataImage extends ResData {
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
    public static class ResDataChain extends ResData {
        private List<ResData> list;

        public ResDataChain(List<ResData> list) {
            this.list = list;
            this.type = "chain";
        }
    }
}
