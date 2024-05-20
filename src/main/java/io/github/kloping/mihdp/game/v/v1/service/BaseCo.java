package io.github.kloping.mihdp.game.v.v1.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.mihdp.dao.Character;
import io.github.kloping.mihdp.dao.Cycle;
import io.github.kloping.mihdp.game.GameStaticResourceLoader;
import io.github.kloping.mihdp.game.api.Addition;
import io.github.kloping.mihdp.game.dao.CharacterInfo;
import io.github.kloping.mihdp.game.service.AdditionLogic;
import io.github.kloping.mihdp.game.v.RedisSource;
import io.github.kloping.mihdp.mapper.CharacterMapper;
import io.github.kloping.mihdp.mapper.CycleMapper;

import java.util.List;

/**
 * 基本的计算服务
 * @author github.kloping
 */
@Entity
public class BaseCo {
    @AutoStand
    CharacterMapper charactersMapper;

    public boolean testForC(Character character, Integer maxXp) {
        if (character.getLevel() < 151 && character.getXp() >= maxXp) {
            if (character.getLevel() % 10 == 0) {
                character.setXp(maxXp);
                charactersMapper.updateById(character);
                return false;
            } else {
                character.setXp(character.getXp() - maxXp);
                character.setLevel(character.getLevel() + 1);
                return true;
            }
        }
        return false;
    }


    @AutoStand
    GameStaticResourceLoader resourceLoader;
    @AutoStand
    AdditionLogic additionLogic;
    @AutoStand
    CycleMapper cycleMapper;
    @AutoStand
    RedisSource redisSource;

    /**
     * 计算 魂角最大血量 经验
     *
     * @param character
     * @return
     */
    public CharacterOutResult compute(Character character) {
        //基础的魂角属性
        CharacterInfo characterInfo = resourceLoader.getCharacterInfoById(character.getCid());
        if (characterInfo == null) return null;
        for (Addition addition : additionLogic.getAddition(character)) {
            characterInfo.reg(addition);
        }
        //魂环加成
        QueryWrapper<Cycle> qw1 = new QueryWrapper<>();
        qw1.eq("cid", character.getId());
        List<Cycle> cycles = cycleMapper.selectList(qw1);
        for (Cycle cycle : cycles) {
            for (Addition addition : additionLogic.getAddition(cycle)) {
                characterInfo.reg(addition);
            }
        }
        characterInfo.setLevel(character.getLevel());
        int maxHp = characterInfo.getHp().getFinalValue();
        int maxXp = characterInfo.getXp().getFinalValue();
        redisSource.str2int.setValue("cid-hp-" + character.getId(), maxHp);
        redisSource.str2int.setValue("cid-xp-" + character.getId(), maxXp);
        return new CharacterOutResult(maxHp, maxXp, characterInfo);
    }

    public static class CharacterOutResult {
        public final Integer maxHp;
        public final Integer maxXp;
        public final CharacterInfo characterInfo;

        public CharacterOutResult(Integer maxHp, Integer maxXp, CharacterInfo characterInfo) {
            this.maxHp = maxHp;
            this.maxXp = maxXp;
            this.characterInfo = characterInfo;
        }
    }
}
