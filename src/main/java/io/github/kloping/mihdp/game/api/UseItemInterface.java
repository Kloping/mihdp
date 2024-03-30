package io.github.kloping.mihdp.game.api;

/**
 * 物品使用映射
 *
 * @author github.kloping
 */
public interface UseItemInterface<T, R> {
    /**
     * 执行c次
     *
     * @param c
     * @param t
     * @return
     */
    R execute(int c, T t);
}
