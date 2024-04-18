package io.github.kloping.mihdp.game.entity;

import lombok.Data;

/**
 * 一个效果
 *
 * @author github.kloping
 */
@Data
public class Eff {
    private Integer type;
    private Integer value;

    public Eff(Integer type, Integer value) {
        this.type = type;
        this.value = value;
    }
}
