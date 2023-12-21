package io.github.kloping.mihdp.game.s.s0;

import com.alibaba.fastjson.JSONObject;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.AutoStandAfter;
import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.mihdp.game.dao.Item;
import io.github.kloping.mihdp.game.s.e0.BaseCharacterInfo;
import io.github.kloping.mihdp.game.s.e0.CharactersInfo;
import io.github.kloping.mihdp.game.s.e0.ItemImpl;
import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author github.kloping
 */
@Entity
public class GameStaticResourceLoader {
    @AutoStand(id = "defaultConfig")
    JSONObject defaultConfig;

    public Shopping shopping = null;

    public Map<Integer, Item> ITEM_MAP = new HashMap<>();

    public BaseCharacterInfo baseCharacterInfo = null;

    private List<CharactersInfo> charactersInfos = new LinkedList<>();
    private Map<String, CharactersInfo> name2charactersInfo = new HashMap<>();
    private Map<Integer, CharactersInfo> id2charactersInfo = new HashMap<>();

    @AutoStandAfter
    public void after() {
        shopping = defaultConfig.getJSONObject("shopping").toJavaObject(Shopping.class);

        List<ItemImpl> items = defaultConfig.getJSONArray("items").toJavaList(ItemImpl.class);

        for (ItemImpl item : items) {
            ITEM_MAP.put(item.getId(), item);
        }

        baseCharacterInfo = defaultConfig.getJSONObject("base_character").toJavaObject(BaseCharacterInfo.class);

        charactersInfos = defaultConfig.getJSONArray("characters").toJavaList(CharactersInfo.class);
        for (CharactersInfo charactersInfo : charactersInfos) {
            name2charactersInfo.put(charactersInfo.getName(), charactersInfo);
            id2charactersInfo.put(charactersInfo.getId(), charactersInfo);
        }
    }

    @Data
    public static class Shopping {
        private Integer rp;
        private Integer rp_max;
    }
}
