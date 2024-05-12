package io.github.kloping.mihdp.game.scenario;

import io.github.kloping.mihdp.game.service.LivingEntity;
import io.github.kloping.number.NumberUtils;

/**
 * @author github.kloping
 */
public class ScenarioImpl implements Scenario {
    /**
     * 场景a方
     */
    LivingEntity[] as;
    /**
     * 场景b方
     */
    LivingEntity[] bs;
    /**
     * 默认场景距离1w m
     */
    public static final Integer MAX_JOURNEY = 10000;

    @Override
    public void run() {
        //循环 直至下一个实体行动
        while (true) {
            allV1();
        }
    }

    private void step(LivingEntity e) {
        step(e, 100);
    }

    private void step(LivingEntity e, Integer bv) {
        int d0 = NumberUtils.percentTo(bv, e.getSpeed().getFinalValue()).intValue();
        e.distance = e.distance - d0;
        if (e.distance == 0) {
            e.letDo(this);
            e.distance = MAX_JOURNEY;
        }
    }

    private LivingEntity hasNv() {
        for (LivingEntity e : as) {
            if (e.getDistance() < e.getSpeed().getFinalValue()) {
                return e;
            }
        }
        for (LivingEntity e : bs) {
            if (e.getDistance() < e.getSpeed().getFinalValue()) {
                return e;
            }
        }
        return null;
    }

    public void allV1() {
        LivingEntity e0 = hasNv();
        int bv = 100;
        if (e0 != null) {
            bv = NumberUtils.toPercent(e0.getDistance(), e0.getSpeed().getFinalValue());
        }
        for (LivingEntity e : as) {
            step(e, bv);
        }
        for (LivingEntity e : bs) {
            step(e, bv);
        }
    }

    @Override
    public void destroy() {

    }
}
