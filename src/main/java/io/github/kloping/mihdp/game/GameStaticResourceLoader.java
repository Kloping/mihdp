package io.github.kloping.mihdp.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.AutoStandAfter;
import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.mihdp.game.dao.BaseCharacterInfo;
import io.github.kloping.mihdp.game.dao.CharacterInfo;
import io.github.kloping.mihdp.game.dao.Item;
import io.github.kloping.mihdp.game.dao.Shopping;
import io.github.kloping.number.NumberUtils;
import lombok.Getter;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * @author github.kloping
 */
@Entity
public class GameStaticResourceLoader {

    public Shopping shopping = null;

    public Map<Integer, Item> itemMap = new LinkedHashMap<>();

    @Getter
    private BaseCharacterInfo baseCharacterInfo = null;
    @Getter
    private BaseCharacterInfo baseEr = null;

    private List<CharacterInfo> charactersInfos = new LinkedList<>();
    private Map<String, CharacterInfo> name2charactersInfo = new HashMap<>();

    public CharacterInfo getCharacterInfoByName(String name) {
        CharacterInfo characterInfo = name2charactersInfo.get(name);
        if (characterInfo != null) {
            return JSON.parseObject(JSON.toJSONString(characterInfo), CharacterInfo.class);
        } else return null;
    }

    private Map<Integer, CharacterInfo> id2charactersInfo = new HashMap<>();

    public CharacterInfo getCharacterInfoById(Integer id) {
        CharacterInfo characterInfo = id2charactersInfo.get(id);
        if (characterInfo != null) {
            return JSON.parseObject(JSON.toJSONString(characterInfo), CharacterInfo.class);
        } else return null;
    }

    @AutoStand
    Gson gson;

    /**
     * 静态配置加载
     */
    @AutoStandAfter
    private void after(JSONObject defaultConfig) {
        if (defaultConfig == null) return;
        shopping = defaultConfig.getJSONObject("shopping").toJavaObject(Shopping.class);

        List<Item> items = defaultConfig.getJSONArray("items").toJavaList(Item.class);

        for (Item item : items) {
            itemMap.put(item.getId(), item);
        }

        JSONObject baseCharacter = defaultConfig.getJSONObject("base_character");
        baseCharacterInfo = gson.fromJson(baseCharacter.toJSONString(), BaseCharacterInfo.TYPE_TOKEN);
        baseEr = gson.fromJson(defaultConfig.getJSONObject("base_er").toJSONString(), BaseCharacterInfo.TYPE_TOKEN);

        JSONArray array = defaultConfig.getJSONArray("characters");
        for (Object o : array) {
            JSONObject oe = (JSONObject) o;
            oe.putAll(baseCharacter);
            CharacterInfo charactersInfo = gson.fromJson(oe.toJSONString(), CharacterInfo.class);
            name2charactersInfo.put(charactersInfo.getName(), charactersInfo);
            id2charactersInfo.put(charactersInfo.getId(), charactersInfo);
            charactersInfos.add(charactersInfo);
        }
    }

    public Map<Integer, File> id2file = new HashMap<>();

    public File getFileById(Integer id) {
        return id2file.get(id);
    }

    /**
     * 静态图片资源加载
     */
    @AutoStandAfter
    private void after1() throws Exception {
        URL enumeration = this.getClass().getClassLoader().getResource("sources/");
        if (enumeration.getProtocol().equals("file")){
            File file = new File(enumeration.getPath());
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
}
