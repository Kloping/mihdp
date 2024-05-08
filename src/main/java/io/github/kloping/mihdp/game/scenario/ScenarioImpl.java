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

        }
    }

    public void allV1() {
      
    }

    @Override
    public void destroy() {

    }
}
