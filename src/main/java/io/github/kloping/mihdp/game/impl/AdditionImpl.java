package io.github.kloping.mihdp.game.impl;

import io.github.kloping.mihdp.game.api.Addition;

/**
 * @author github.kloping
 */
public class AdditionImpl implements Addition {
    private String attr;
    private Integer type = 1;
    private Integer value = 0;

    @Override
    public String getAttr() {
        return attr;
    }

    @Override
    public Integer getType() {
        return type;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public AdditionImpl(String attr) {
        this.attr = attr;
    }

    /**
     * @param attr 属性名
     * @param type 1基础值 2百分值
     * @param value
     */
    public AdditionImpl(String attr, Integer type, Integer value) {
        this.attr = attr;
        this.type = type;
        this.value = value;
    }

    public AdditionImpl(String attr, Integer value) {
        this.attr = attr;
        this.type = 2;
        this.value = value;
    }
}
