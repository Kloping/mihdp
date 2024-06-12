package io.github.kloping.mihdp.game.service.effs;

import io.github.kloping.mihdp.game.service.Eff;
import io.github.kloping.mihdp.game.service.EffResult;
import io.github.kloping.mihdp.game.service.LivingEntity;
import io.github.kloping.number.NumberUtils;
import io.github.kloping.rand.RandomUtils;

/**
 * @author github.kloping
 */
public class AttEff extends Eff {

    public static final int TYPE = 1;

    public AttEff(Integer value) {
        super(TYPE, value);
    }

    public static Integer ATT_BV_N = 60;

    /**
     * 防御效用 y = 1- (c/c+x)
     */
    public static final Integer DEFENSE_EFF = 1000;

    @Override
    public EffResult fun(LivingEntity entity0, LivingEntity entity1) {
        int state = 0;
        //获得攻击百分比值
        final Integer v0 = NumberUtils.percentTo(ATT_BV_N, entity0.getAtt().getFinalValue()).intValue();
        //计算防御效用
        Integer dv = 1 - (DEFENSE_EFF / (DEFENSE_EFF + entity1.getDefense().getFinalValue()));
        //获得现在应该造成的伤害
        Integer v1 = NumberUtils.percentTo(100 - dv, v0).intValue();
        //计算暴击
        Integer cc = entity0.getChc().getFinalValue();
        if (RandomUtils.RANDOM.nextInt(100) < cc) {
            //暴击了 爆伤加成
            Integer cbv = entity0.getChe().getFinalValue();
            //计算加成
            v1 = v1 + NumberUtils.percentTo(cbv, v1).intValue();
            state = 3;
        }
        entity1.asHp(-v1);
        return new EffResult(TYPE, v0, v1, state);
    }
}
