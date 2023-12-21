package io.github.kloping.mihdp.game.api.logic;

/**
 * A1 参数 与 A2 参数的逻辑结果是 R0
 * @author github.kloping
 */
public interface LogicBase<A1, A2, R0> {
    /**
     * 处理
     *
     * @param a1
     * @param a2
     * @return
     */
    R0 logic(A1 a1, A2 a2);
}
