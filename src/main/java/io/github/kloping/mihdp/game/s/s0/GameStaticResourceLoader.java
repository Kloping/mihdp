package io.github.kloping.mihdp.game.s.s0;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.kloping.MySpringTool.annotations.AutoStandAfter;
import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.mihdp.game.dao.Item;
import io.github.kloping.mihdp.game.dao.Shopping;
import io.github.kloping.mihdp.game.impl.ItemImpl;
import io.github.kloping.mihdp.game.s.BaseCharacterInfo;
import io.github.kloping.mihdp.game.s.CharactersInfo;
import io.github.kloping.number.NumberUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author github.kloping
 */
@Entity
public class GameStaticResourceLoader {

    public Shopping shopping = null;

    public Map<Integer, Item> ITEM_MAP = new HashMap<>();

    public BaseCharacterInfo baseCharacterInfo = null;
    public BaseCharacterInfo baseEr = null;

    public List<CharactersInfo> charactersInfos = new LinkedList<>();
    public Map<String, CharactersInfo> name2charactersInfo = new HashMap<>();
    public Map<Integer, CharactersInfo> id2charactersInfo = new HashMap<>();

    /**
     * 静态配置加载
     */
    @AutoStandAfter
    private void after(JSONObject defaultConfig) {
        if (defaultConfig == null) return;
        shopping = defaultConfig.getJSONObject("shopping").toJavaObject(Shopping.class);

        List<ItemImpl> items = defaultConfig.getJSONArray("items").toJavaList(ItemImpl.class);

        for (ItemImpl item : items) {
            ITEM_MAP.put(item.getId(), item);
        }

        JSONObject baseCharacter = defaultConfig.getJSONObject("base_character");
        baseCharacterInfo = baseCharacter.toJavaObject(BaseCharacterInfo.class);
        baseEr = defaultConfig.getJSONObject("base_er").toJavaObject(BaseCharacterInfo.class);

        JSONArray array = defaultConfig.getJSONArray("characters");
        for (Object o : array) {
            JSONObject oe = (JSONObject) o;
            oe.putAll(baseCharacter);
            CharactersInfo charactersInfo = oe.toJavaObject(CharactersInfo.class);
            name2charactersInfo.put(charactersInfo.getName(), charactersInfo);
            id2charactersInfo.put(charactersInfo.getId(), charactersInfo);
            charactersInfos.add(charactersInfo);
        }
    }

    public Map<Integer, File> id2file = new HashMap<>();

    /**
     * 静态图片资源加载
     */
    @AutoStandAfter
    private void after1() {
        String path = new ClassPathResource("sources").getPath();
        File file = new File(path);
        if (file.isDirectory()) {
            for (File e : file.listFiles()) {
                Integer id = NumberUtils.getIntegerFromString(e.getName(), -1);
                if (id >= 0) {
                    id2file.put(id, e);
                }
            }
        }
    }
}
