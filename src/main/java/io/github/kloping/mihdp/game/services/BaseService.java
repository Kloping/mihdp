package io.github.kloping.mihdp.game.services;

import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.mihdp.ex.GeneralData;

import java.util.HashMap;
import java.util.Map;

/**
 * @author github.kloping
 */
@Entity
public class BaseService {
    public static final Map<String, String> MSG2ACTION = new HashMap<>();

    public static final String[] BASE_ICON_ARGS = {"icon", "src"};

    static {
        MSG2ACTION.put("测试", "test");
    }

    /**
     * 将 data转为action 或者null
     * @param resData
     * @return
     */
    public static String trnasActionOrNull(GeneralData resData) {
        if (resData instanceof GeneralData.ResDataText) {
            GeneralData.ResDataText text = (GeneralData.ResDataText) resData;
            String content = text.getContent().trim();
            if (content.matches("\\d")) {
                return "s" + content.trim();
            } else return MSG2ACTION.get(content);
        }
        return null;
    }
}
