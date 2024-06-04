package io.github.kloping.mihdp.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author github.kloping
 */
public class DefConfig {
    private JSONObject data;

    public DefConfig(JSONObject data) {
        this.data = data;
    }

    public <T> T getObj(String key, Class<T> cls, T def) {
        if (data.containsKey(key)) {
            return (T) data.get(key);
        } else return def;
    }
}
