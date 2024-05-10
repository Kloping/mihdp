package io.github.kloping.mihdp.game.scenario;

import io.github.kloping.mihdp.game.service.LivingEntity;

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
        e.distance = e.distance - e.getSpeed().getFinalValue();
        if (e.distance == 0) {
            e.letDo(this);
            e.distance = MAX_JOURNEY;
        }
    }

    private boolean hasNv() {
        for (LivingEntity a : as) {
            if (a.getDistance() < a.getSpeed().getFinalValue()) {
                return true;
            }
        }
        for (LivingEntity b : bs) {
            if (b.getDistance() < b.getSpeed().getFinalValue()) {
                return true;
            }
        }
        return false;
    }

    public void allV1() {
        if (hasNv()) {

        } else {
            for (LivingEntity e : as) {
                step(e);
            }
            for (LivingEntity e : bs) {
                step(e);
            }
        }
    }

    @Override
    public void destroy() {

    }
}
