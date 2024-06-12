package io.github.kloping.mihdp.game.service;

import lombok.Data;

/**
 * 效果后的结果
 * @author github.kloping
 */
@Data
public class EffResult {
    public static final EffResult RESULT0 = new EffResult(0, 0, 0, 1);

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
     * 状态 0.默认已命中,1.未生效,2.被阻挡,3,暴击生效
     */
    private Integer state = 0;

    public EffResult(Integer type, Integer oValue, Integer value, Integer state) {
        this.type = type;
        this.oValue = oValue;
        this.value = value;
        this.state = state;
    }

    private String tips = "";

    public String getStateTips() {
        switch (state) {
            case 0:
                return "生效";
            case 1:
                return "未生效";
            case 2:
                return "被阻拦";
            case 3:
                return "暴击生效";
            default:
                return "未知";
        }
    }
}
