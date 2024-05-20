package io.github.kloping.mihdp.game.dao;

import io.github.kloping.number.NumberUtils;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author github.kloping
 */
@Data
@Accessors(chain = true)
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
    private Integer bv = 100;
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

    public Attr copy() {
        Attr attr = new Attr(name, value);
        attr.bv = bv;
        attr.baseValue = baseValue;
        return attr;
    }
}
