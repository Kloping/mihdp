package io.github.kloping.mihdp.game.service.csn;

import io.github.kloping.mihdp.game.scenario.Scenario;
import io.github.kloping.mihdp.game.service.Eff;
import io.github.kloping.mihdp.game.service.EffResult;
import io.github.kloping.mihdp.game.service.LivingEntity;
import io.github.kloping.mihdp.game.v.v1.service.BaseCo;

import java.util.concurrent.CountDownLatch;

/**
 * 魂角作战 基础
 *
 * @author github.kloping
 */
public class CiBase extends LivingEntity {

    public static CiBase create(BaseCo.CharacterOutResult result) {
        CiBase base = new CiBase();
        base.maxHp = result.characterInfo.getHp().copy();
        base.hp = base.maxHp.getFinalValue();
        base.att = result.characterInfo.getAtt().copy();
        base.defense = result.characterInfo.getDefense().copy();
        base.speed = result.characterInfo.getSpeed().copy();
        base.chc = result.characterInfo.getChc().copy();
        base.che = result.characterInfo.getChe().copy();
        base.efh = result.characterInfo.getEfh().copy();
        base.efr = result.characterInfo.getEfr().copy();
        base.distance = 1000;
        return base;
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
        return "";
    }

    @Override
    public Object letDoPre(Scenario scenario, CountDownLatch cdl, LivingEntity[] as) {
        return "请操作!";
    }
}
