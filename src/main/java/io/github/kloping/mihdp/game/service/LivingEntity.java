package io.github.kloping.mihdp.game.service;

import io.github.kloping.mihdp.game.dao.Attr;
import io.github.kloping.mihdp.game.scenario.ScenarioImpl;
import lombok.Getter;

/**
 * 一个活的实体 可以是 怪物或者玩家
 *
 * @author github.kloping
 */
@Getter
public abstract class LivingEntity {
    /**
     * 0.默认 正常状态 1.被眩晕 2.被冰冻
     */
    private Integer state = 0;

    private Integer hp;

    protected Attr maxHp;
    protected Attr att;
    /**
     * 防御
     */
    protected Attr defense;
    /**
     * 速度
     */
    protected Attr speed;
    /**
     * 效果命中
     */
    protected Attr efr;
    /**
     * 效果抵抗
     */
    protected Attr efh;
    /**
     * 距离仅场景中使用
     */
    public Integer distance = 0;
    /**
     * 对指定
     *
     * @param eff
     * @param entity
     * @return
     */
    public abstract EffResult eff(Eff eff, LivingEntity entity);

    /**
     * 附加负面
     *
     * @param type
     * @param entity
     * @return
     */
    public abstract boolean attach(Integer type, LivingEntity entity);

    /**
     * 行动中
     *
     * @param scenario
     */
    public abstract void letDo(ScenarioImpl scenario);
}
