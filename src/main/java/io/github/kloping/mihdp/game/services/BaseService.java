package io.github.kloping.mihdp.game.services;

import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import io.github.kloping.mihdp.wss.data.ResDataPack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author github.kloping
 */
@Entity
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
        GeneralData generalData = (GeneralData) pack.getArgs().get(GameClient.ODATA_KEY);
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
            for (GeneralData data : chain.getList()) {
                GeneralData.ResDataText text = (GeneralData.ResDataText) generalData;
                String content = text.getContent().trim();
                if (content.matches("\\d")) {
                    text.setContent(null);
                    action = "s" + content.trim();
                } else if (MSG2ACTION.containsKey(content)) {
                    text.setContent(null);
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
            }
        }
        if (action != null) {
            System.err.println("conversion action: " + action);
            pack.setAction(action);
        }
        return pack;
    }
}
