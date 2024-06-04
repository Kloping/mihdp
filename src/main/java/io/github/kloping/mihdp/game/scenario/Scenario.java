package io.github.kloping.mihdp.game.scenario;

import io.github.kloping.MySpringTool.interfaces.component.ContextManager;
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
    Object out(ContextManager context);

    /**
     * ce 当前行动的们
     *
     * @return
     */
    LivingEntity[] getCurrentEntities(String id);

    /**
     * 获取控制的实体
     *
     * @return
     */
    LivingEntity getCurrentEntity(String id);

    /**
     * 获得提示
     *
     * @return
     */
    Object getTips(ContextManager context);
}
