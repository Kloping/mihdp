package io.github.kloping.mihdp.game.service;

import lombok.Data;

/**
 * 一个效果 可以是攻击 可以是控制 提速 或 减速
 *
 * @author github.kloping
 */
@Data
public abstract class Eff {
    /**
     * 防御效用 y = 1- (c/c+x)
     */
    public static final Integer DEFENSE_EFF = 1000;


    private Integer type;
    private Integer value;

    public Eff(Integer type, Integer value) {
        this.type = type;
        this.value = value;
    }

    public abstract EffResult fun(LivingEntity entity0, LivingEntity entity1);
}
