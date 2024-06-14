package io.github.kloping.mihdp.game.v.v1.service;

import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.arr.Class2OMap;
import io.github.kloping.mihdp.dao.Character;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.dao.UsersResources;
import io.github.kloping.mihdp.game.GameStaticResourceLoader;
import io.github.kloping.mihdp.game.api.ItemUseContext;
import io.github.kloping.mihdp.game.v.RedisSource;
import io.github.kloping.mihdp.game.v.v0.InfoController;
import io.github.kloping.mihdp.game.v.v1.CharactersController;
import io.github.kloping.mihdp.game.v.v1.ItemAboController;
import io.github.kloping.mihdp.mapper.CharacterMapper;
import io.github.kloping.mihdp.mapper.UsersResourcesMapper;
import io.github.kloping.number.NumberUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author github.kloping
 */
@Entity
public class ItemOf {
    @AutoStand
    CharactersController charactersController;
    @AutoStand
    RedisSource redisSource;
    @AutoStand
    CharacterMapper charactersMapper;
    @AutoStand
    GameStaticResourceLoader resourceLoader;
    @AutoStand
    UsersResourcesMapper usersResourcesMapper;
    @AutoStand
    BaseCo baseCi;
    @AutoStand
    InfoController infoController;

    public final Map<Integer, ItemUseContext> CONTEXT_MAP = new HashMap<>();

    {
        CONTEXT_MAP.put(101, cc -> {
            return addXpOfItem(cc, 50);
        });
        CONTEXT_MAP.put(102, cc -> {
            return addXpOfItem(cc, 200);
        });
        CONTEXT_MAP.put(103, cc -> {
            Integer c = cc.get(Integer.class);
            User user = cc.get(User.class);
            UsersResources resources = usersResourcesMapper.selectById(user.getUid());
            for (Integer i = 0; i < c; i++) {
                resources.setEnergy(resources.getEnergy() + 160);
            }
            resources.applyE(redisSource);
            usersResourcesMapper.updateById(resources);
            return new ItemAboController.UseState(true, "成功,请查看信息.tips:灵力大于等于上限不在回复", 1);
        });
        CONTEXT_MAP.put(104, cc -> {
            Integer c = cc.get(Integer.class);
            User user = cc.get(User.class);
            Character character = charactersController.getCharacterOrLowestLevel(user.getUid());
            if (character == null) return new ItemAboController.UseState(false, "未拥有任何魂角", 0);
            int maxHp = baseCi.compute(character).maxHp;

            int n = 0;
            for (Integer i = 0; i < c; i++) {
                Integer v0 = NumberUtils.percentTo(15, maxHp).intValue();
                character.setHp(character.getHp() + v0);
                n++;
                if (character.getHp() >= maxHp) {
                    character.setHp(maxHp);
                    charactersMapper.updateById(character);
                    return new ItemAboController.UseState(true, "使用完成;当前魂角血量100%", n);
                }
            }
            if (n > 0) charactersMapper.updateById(character);
            return new ItemAboController.UseState(true, "使用完成;当前魂角血量" + NumberUtils.toPercent(character.getHp(), maxHp) + "%", n);
        });
    }

    private ItemAboController.UseState addXpOfItem(Class2OMap cc, int x) {
        User user = cc.get(User.class);
        Integer c = cc.get(Integer.class);
        Character character = charactersController.getCharacterOrLowestLevel(user.getUid());
        if (character == null) return new ItemAboController.UseState(false, "未拥有任何魂角", 0);
        int maxXp = baseCi.compute(character).maxXp;
        int xpa = 0, levela = 0;
        int c0 = 0;
        for (int i = 0; i < c; i++) {
            xpa += x;
            c0++;
            character.setXp(character.getXp() + x);
            if (character.getXp() >= maxXp) {
                if (baseCi.testForC(character, maxXp)) {
                    maxXp = baseCi.compute(character).maxXp;
                    levela++;
                } else {
                    return new ItemAboController.UseState(true, "经验上限喽\n再次升级需要吸收魂环.", c0);
                }
            }
        }
        charactersMapper.updateById(character);
        return new ItemAboController.UseState(true, String.format("对魂角(%s)使用完成(%s).\n累计增加了%s经验值\n增加了%s级",
                resourceLoader.getCharacterInfoById(character.getCid()).getName(), c, xpa, levela), c);
    }
}
