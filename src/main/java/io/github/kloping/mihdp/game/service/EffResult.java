package io.github.kloping.mihdp.game.service;

import lombok.Data;

/**
 * 效果后的结果
 * @author github.kloping
 */
@Data
public class EffResult {
    private Integer type;
    /**
     * 原始值
     */
    private final Integer oValue;
    /**
     * 生效值
     */
    private Integer value;
    /**
     * 状态 0.默认已命中,1.未生效,2.被阻挡
     */
    private Integer state = 0;

    public EffResult(Integer type, Integer oValue, Integer value, Integer state) {
        this.type = type;
        this.oValue = oValue;
        this.value = value;
        this.state = state;
    }
}
