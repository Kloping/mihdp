package io.github.kloping.mihdp.game.api;

/**
 * 加成
 * @author github.kloping
 */
public interface Addition {

    /**
     * 来源
     *
     * @return
     */
    default Object getSource() {
        return null;
    }

    /**
     * 要加成的项
     *
     * @return
     */
    String getAttr();

    /**
     * 加成类型 1.基础值 2.百分比值
     *
     * @return
     */
    Integer getType();

    /**
     * 加成的值
     *
     * @return
     */
    Integer getValue();
}
