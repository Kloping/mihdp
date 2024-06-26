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
import lombok.Getter;

import java.util.List;

/**
 * 基本的计算服务
 * @author github.kloping
 */
@Entity
public class BaseCo {
    @AutoStand
    CharacterMapper charactersMapper;

    /**
     * 检测 魂角是否符合并升级
     *
     * @param character
     * @param maxXp
     * @return true 升级了 false 最大或上限
     */
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
    public CharacterResult compute(Character character) {
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
        Integer hl = redisSource.str2int.getValue("cid-hl-" + character.getCid());
        if (hl == null) hl = characterInfo.getHl().getFinalValue();
        Integer hj = redisSource.str2int.getValue("cid-hj-" + character.getCid());
        if (hj == null) hj = characterInfo.getHj().getFinalValue();

        return new CharacterResult(maxHp, maxXp, hl, hj, characterInfo, character, redisSource);
    }

    public static class CharacterResult {
        @Getter
        public Integer hj;
        @Getter
        public Integer hl;
        public final Integer maxHp;
        public final Integer maxXp;
        public final CharacterInfo characterInfo;
        public final Character character;

        private RedisSource redisSource;

        public CharacterResult(Integer maxHp, Integer maxXp, Integer hl, Integer hj, CharacterInfo characterInfo, Character character, RedisSource redisSource) {
            this.maxHp = maxHp;
            this.maxXp = maxXp;
            this.hl = hl;
            this.hj = hj;
            this.characterInfo = characterInfo;
            this.character = character;
            this.redisSource = redisSource;
        }

        public CharacterResult setHj(Integer hj) {
            int max = characterInfo.getHj().getFinalValue();
            hj = hj < 0 ? 0 : hj > max ? max : hj;
            redisSource.str2int.setValue("cid-hj-" + character.getId(), hj);
            this.hj = hj;
            return this;
        }

        public CharacterResult setHl(Integer hl) {
            int max = characterInfo.getHj().getFinalValue();
            hl = hl < 0 ? 0 : hl > max ? max : hl;
            redisSource.str2int.setValue("cid-hl-" + character.getId(), hl);
            this.hl = hl;
            return this;
        }
    }
}
