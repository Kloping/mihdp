package io.github.kloping.mihdp.game.service;

import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.mihdp.dao.Character;
import io.github.kloping.mihdp.dao.Cycle;
import io.github.kloping.mihdp.game.GameStaticResourceLoader;
import io.github.kloping.mihdp.game.api.Addition;
import io.github.kloping.mihdp.game.api.logic.LogicBase;
import io.github.kloping.mihdp.game.dao.CharacterInfo;
import io.github.kloping.mihdp.game.impl.AdditionImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 属性加成
 * @author github.kloping
 */
@Entity
public class AdditionLogic implements LogicBase {

    public Map<Integer, Addition[]> ADDITION_MAP = new HashMap<>();

    {
        // 2001-2007: 魂环++
        ADDITION_MAP.put(2001, new Addition[]{new AdditionImpl("hp", 5), new AdditionImpl("att", 5)});
        ADDITION_MAP.put(2002, new Addition[]{new AdditionImpl("hp", 10), new AdditionImpl("att", 5), new AdditionImpl("hl", 5)});
        ADDITION_MAP.put(2003, new Addition[]{new AdditionImpl("hp", 10), new AdditionImpl("att", 15), new AdditionImpl("hl", 5)});
        ADDITION_MAP.put(2004, new Addition[]{new AdditionImpl("hp", 15), new AdditionImpl("att", 15), new AdditionImpl("hl", 5), new AdditionImpl("hj", 5)});
        ADDITION_MAP.put(2005, new Addition[]{new AdditionImpl("hp", 10), new AdditionImpl("att", 20), new AdditionImpl("hl", 10), new AdditionImpl("hj", 5)});
        ADDITION_MAP.put(2006, new Addition[]{new AdditionImpl("hp", 15), new AdditionImpl("att", 20), new AdditionImpl("hl", 15), new AdditionImpl("hj", 10)});
        ADDITION_MAP.put(2007, new Addition[]{new AdditionImpl("hp", 20), new AdditionImpl("att", 25), new AdditionImpl("hl", 10), new AdditionImpl("hj", 15)});
    }

    /**
     * 魂环加成获取
     *
     * @param cycle
     * @return
     */
    public Addition[] getAddition(Cycle cycle) {
        return ADDITION_MAP.get(cycle.getOid());
    }

    @AutoStand
    GameStaticResourceLoader resourceLoader;

    /**
     * 等级加成获取
     *
     * @param character
     * @return
     */
    public Addition[] getAddition(Character character) {
        List<Addition> result = new LinkedList<>();
        int level = character.getLevel();
        int b = level - 1;
        if (b > 0) {
            result.add(new AdditionImpl("xp", 1, resourceLoader.getBaseEr().getXp().getValue() * b));
            result.add(new AdditionImpl("hp", 1, resourceLoader.getBaseEr().getHp().getValue() * b));
            result.add(new AdditionImpl("hl", 1, resourceLoader.getBaseEr().getHl().getValue() * b));
            result.add(new AdditionImpl("hj", 1, resourceLoader.getBaseEr().getHj().getValue() * b));
            result.add(new AdditionImpl("att", 1, resourceLoader.getBaseEr().getAtt().getValue() * b));
            result.add(new AdditionImpl("defense", 1, resourceLoader.getBaseEr().getAtt().getValue() * b));
        }
        int b0 = level / 10;
        if (b0 > 0) {
            CharacterInfo charactersInfo = resourceLoader.getCharacterInfoById(character.getCid());
            result.add(new AdditionImpl(charactersInfo.getBt(), 2, charactersInfo.getBtv() * b0));
            result.add(new AdditionImpl("speed", 2, b0));
        }
        return result.toArray(new Addition[0]);
    }
}
