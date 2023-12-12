package io.github.kloping.mihdp.wss.data;

import com.alibaba.fastjson.JSON;
import io.github.kloping.mihdp.ex.GeneralData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author github.kloping
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResDataPack {
    private String action;

    private String id;
    private String bot_id;
    private String env_type;
    private String env_id = "";
    private GeneralData data;
    private Long time = 0L;

    private Map<String, Object> args = new HashMap<>();

    public boolean containsArgs(String... names) {
        for (String name : names) {
            if (args.get(name) != null) return true;
        }
        return false;
    }

    public Object getArgValue(String... names) {
        for (String name : names) {
            Object v = args.get(name);
            if (v != null) return v;
        }
        return null;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
