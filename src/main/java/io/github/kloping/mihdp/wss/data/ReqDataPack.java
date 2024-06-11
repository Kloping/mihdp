package io.github.kloping.mihdp.wss.data;

import com.alibaba.fastjson.JSON;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.wss.GameClient;
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
public class ReqDataPack {
    private String action;

    private String id;

    private String bot_id;
    private String env_type;
    private String env_id = "";
    private String sender_id;
    private String content;

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

    public boolean isArgValue(String name, Object v) {
        Object o = args.get(name);
        if (o != null) return o.toString().equals(v.toString());
        return false;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    private GameClient client;

    public void set(GameClient gameClient) {
        this.client = client;
    }

    public void send(GeneralData data) {
        client.send(this, data);
    }
}
