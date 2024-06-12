package io.github.kloping.mihdp.game.service.csn;

import io.github.kloping.mihdp.game.scenario.Scenario;
import io.github.kloping.mihdp.game.service.Eff;
import io.github.kloping.mihdp.game.service.EffResult;
import io.github.kloping.mihdp.game.service.LivingEntity;
import io.github.kloping.mihdp.game.service.effs.AttEff;
import io.github.kloping.mihdp.game.v.v1.service.BaseCo;
import io.github.kloping.number.NumberUtils;

import java.util.concurrent.CountDownLatch;

/**
 * 魂角作战 基础
 *
 * @author github.kloping
 */
public class CiBase extends LivingEntity {

    /**
     * 控制者id
     */
    public String fid;

    private boolean prep = false;
    /**
     * 操作码 -1 等于无
     * 0 攻击
     * 1 撤离
     */
    public int op = -1;

    public CountDownLatch cdl = null;

    private CiBase(Integer id, Integer cid) {
        super(id, cid);
    }

    public static CiBase create(BaseCo.CharacterOutResult result) {
        CiBase base = new CiBase(getId(), result.characterInfo.getId());
        base.maxHp = result.characterInfo.getHp().copy();
        base.hp = result.character.getHp();
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
        this.cdl = cdl;
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Object r = null;
        switch (op) {
            case AttEff.TYPE:
                LivingEntity entity = getRecentlyE(as);
                Integer avl = NumberUtils.percentTo(this.getAtt().getFinalValue(), 60).intValue();
                EffResult result = eff(new AttEff(avl), entity);
                r = String.format("玩家使用了普通撞击对指定造成%s点伤害(%s)", result.getValue(), result.getStateTips());
        }
        prep = false;
        return r;
    }

    private LivingEntity getRecentlyE(LivingEntity[] as) {
        for (LivingEntity a : as) {
            if (a.hp > 0) return a;
        }
        return null;
    }

    @Override
    public Object letDoPre(Scenario scenario, CountDownLatch cdl, LivingEntity[] as) {
        prep = true;
        return "请操作!(攻击/撤离)";
    }
}
