package io.github.kloping.mihdp.game.service;

import lombok.Data;

/**
 * 一个效果
 *
 * @author github.kloping
 */
@Data
public abstract class Eff {
    private Integer type;
    private Integer value;

    public Eff(Integer type, Integer value) {
        this.type = type;
        this.value = value;
    }

    public abstract EffResult fun(LivingEntity entity0, LivingEntity entity1);
}