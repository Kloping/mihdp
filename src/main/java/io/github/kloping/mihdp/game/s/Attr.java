package io.github.kloping.mihdp.game.s;

import io.github.kloping.number.NumberUtils;
import lombok.Data;

/**
 * @author github.kloping
 */
@Data
public class Attr {
    /**
     * 属性名
     */
    private final String name;
    /**
     * 初始值
     */
    private final Integer value;
    /**
     * 百分比加成
     */
    private Integer bv;
    /**
     * 基础值
     */
    private Integer baseValue;

    public Attr(String name, Integer value) {
        this.name = name;
        this.value = value;
        this.bv = 100;
        this.baseValue = value;
    }

    public Attr(String name) {
        this(name, 0);
    }

    public Integer getFinalValue() {
        return NumberUtils.percentTo(baseValue, bv).intValue();
    }
}
