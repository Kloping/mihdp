package io.github.kloping.mihdp.game.entity;

/**
 * 效果类型
 *
 * @author github.kloping
 */
public enum EffType {
    /**
     * 伤害
     */
    DAMAGE("伤害"),
    RECOVER("回血"),
    ADDR("伤害增加"),
    MSR("免伤增加"),
    ;

    private final String name;

    EffType(String name) {
        this.name = name;
    }
}
