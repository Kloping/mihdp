package io.github.kloping.mihdp.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author github.kloping
 */
public class LanguageConfig {
    public String local = "zh";
    private JSONObject data = null;

    public LanguageConfig(String local, JSONObject data) {
        this.local = local;
        this.data = data.getJSONObject(local);
    }

    public String getString(String key, Object... args) {
        return String.format(data.getString(key), args);
    }
}
