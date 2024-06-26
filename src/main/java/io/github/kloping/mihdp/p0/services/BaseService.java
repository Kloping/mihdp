package io.github.kloping.mihdp.p0.services;

import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.wss.data.ReqDataPack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author github.kloping
 */
public class BaseService {
    public static final Map<String, String> MSG2ACTION = new HashMap<>();

    public static final String[] BASE_ICON_ARGS = {"icon", "src"};
    public static final String[] BASE_NAME_ARGS = {"NAME", "name", "NICKNAME", "nickname"};

    static {
        MSG2ACTION.put("测试", "test");
    }

    /**
     * 将 data转为action 或者null
     * @param resData
     * @return
     */
    public static ReqDataPack trnasActionOrNull(ReqDataPack pack) {
        GeneralData generalData = pack.getContentAsData();
        String action = null;
        if (generalData instanceof GeneralData.ResDataText) {
            GeneralData.ResDataText text = (GeneralData.ResDataText) generalData;
            String content = text.getContent().trim();
            if (content.matches("\\d")) {
                action = "s" + content.trim();
            } else if (MSG2ACTION.containsKey(content)) {
                action = MSG2ACTION.get(content);
            } else {
                for (String key : MSG2ACTION.keySet()) {
                    if (content.startsWith(key)) {
                        text.setContent(text.getContent().replace(key, ""));
                        action = MSG2ACTION.get(key);
                        break;
                    }
                }
            }
        } else if (generalData instanceof GeneralData.ResDataChain) {
            GeneralData.ResDataChain chain = (GeneralData.ResDataChain) generalData;
            chain = new GeneralData.ResDataChain(chain.filterAt(pack.getBot_id()));
            if (!chain.getList().isEmpty()) {
                GeneralData d0 = chain.getList().get(0);
                if (d0 instanceof GeneralData.ResDataText) {
                    action = toAction((GeneralData.ResDataText) d0, action);
                }
            }
        }
        if (action != null) {
            System.err.println("conversion action: " + action);
            pack.setAction(action);
        }
        return pack;
    }

    private static String toAction(GeneralData.ResDataText d0, String action) {
        GeneralData.ResDataText text = d0;
        String content = text.getContent().trim();
        if (content.matches("\\d")) {
            text.setContent(null);
            action = "s" + content.trim();
        } else if (MSG2ACTION.containsKey(content)) {
            text.setContent("");
            action = MSG2ACTION.get(content);
        } else {
            for (String key : MSG2ACTION.keySet()) {
                if (content.startsWith(key)) {
                    text.setContent(text.getContent().replace(key, "").trim());
                    action = MSG2ACTION.get(key);
                    break;
                }
            }
        }
        return action;
    }
}
