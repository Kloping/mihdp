package io.github.kloping.mihdp.game.scenario;

/**
 * 场景基础
 *
 * @author github.kloping
 */
public interface Scenario extends Runnable {
    /**
     * 场景销毁
     */
    void destroy();
}
