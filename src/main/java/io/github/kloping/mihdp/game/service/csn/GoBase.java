package io.github.kloping.mihdp.game.service.csn;

import io.github.kloping.mihdp.game.dao.Attr;
import io.github.kloping.mihdp.game.scenario.Scenario;
import io.github.kloping.mihdp.game.service.Eff;
import io.github.kloping.mihdp.game.service.EffResult;
import io.github.kloping.mihdp.game.service.LivingEntity;
import io.github.kloping.mihdp.game.service.effs.AttEff;
import io.github.kloping.mihdp.game.service.gos.GoBaseImpl3001;
import io.github.kloping.number.NumberUtils;
import io.github.kloping.rand.RandomUtils;

import java.util.concurrent.CountDownLatch;

/**
 * 魂角作战 基础
 *
 * @author github.kloping
 */
public abstract class GoBase extends LivingEntity {

    public GoBase(Integer id, Integer cid) {
        super(id, cid);
    }

    public static GoBase[] create(Integer level, Integer id) {
        if (id == 3001) {
            return new GoBase[]{new GoBaseImpl3001(RandomUtils.RANDOM.nextInt(20) + level), new GoBaseImpl3001(RandomUtils.RANDOM.nextInt(20) + level),};
        }
        GoBase base = new GoBase(getId(), id) {
            @Override
            public int[] getFallObjs() {
                return new int[]{101, 101};
            }
        };

        base.maxHp = new Attr("hp", level * 100).setBaseValue(level * 100);
        base.att = new Attr("att", 10).setBaseValue(10);
        base.defense = new Attr("defense", 10).setBaseValue(10);

        base.hp = base.maxHp.getFinalValue();
        base.speed = new Attr("speed", 110);
        base.chc = new Attr("chc", 0);
        base.che = new Attr("che", 50);
        base.efh = new Attr("efh", 50);
        base.efr = new Attr("efr", 50);
        base.distance = 1000;
        return new GoBase[]{base};
    }

    @Override
    public EffResult eff(Eff eff, LivingEntity entity) {
        if (entity == null || entity.getHp() <= 0) {
            return new EffResult(eff.getType(), eff.getValue(), eff.getValue(), 1);
        } else if (eff.getValue() > 0) {
            return eff.fun(this, entity);
        }
        return EffResult.RESULT0;
    }

    @Override
    public Object letDo(Scenario scenario, CountDownLatch cdl, LivingEntity[] as) {
        LivingEntity entity = RandomUtils.getRand(as);
        Integer avl = NumberUtils.percentTo(this.getAtt().getFinalValue(), 60).intValue();
        EffResult result = eff(new AttEff(avl), entity);
        return String.format("NPC使用了普通撞击对指定造成%s点伤害(%s)", result.getValue(), result.getStateTips());
    }

    public abstract int[] getFallObjs();
}
