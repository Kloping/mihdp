package io.github.kloping.mihdp.game.scenario;

import io.github.kloping.mihdp.game.service.LivingEntity;

/**
 * 场景基础
 *
 * @author github.kloping
 */
public interface Scenario extends Runnable {
    /**
     * 场景销毁
     */
    void destroy(LivingEntity[] as);

    /**
     * @return
     */
    Object out();


    LivingEntity[] getCurrentEntities();

    /**
     * 获得提示
     *
     * @return
     */
    Object getTips();
}
