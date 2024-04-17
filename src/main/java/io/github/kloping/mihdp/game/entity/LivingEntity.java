package io.github.kloping.mihdp.game.entity;

import lombok.Getter;

/**
 * 一个活的实体 可以是 怪物或者玩家
 *
 * @author github.kloping
 */
@Getter
public abstract class LivingEntity {
    private Integer hp;
    private Integer state;
    /**
     * 对指定
     *
     * @param damage
     * @param entity
     * @return
     */
    public abstract DamageResult ack(Damage damage, LivingEntity entity);

    /**
     * 附加负面
     *
     * @param type
     * @param entity
     * @return
     */
    public abstract boolean attach(Integer type, LivingEntity entity);
}
