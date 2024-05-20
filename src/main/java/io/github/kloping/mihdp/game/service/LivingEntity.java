package io.github.kloping.mihdp.game.service;

import io.github.kloping.mihdp.game.dao.Attr;
import io.github.kloping.mihdp.game.scenario.Scenario;
import lombok.Getter;

import java.util.concurrent.CountDownLatch;

/**
 * 一个活的实体 可以是 怪物或者玩家
 *
 * @author github.kloping
 */
@Getter
public abstract class LivingEntity {
    /**
     * 0.默认 正常状态 1.被眩晕 2.被冰冻 -1 over
     */
    protected Integer state = 0;

    public Integer hp;

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
     * 爆率
     */
    protected Attr chc;
    /**
     * 爆伤
     */
    protected Attr che;
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
     * 主动对指定
     *
     * @param eff
     * @param entity
     * @return
     */
    public abstract EffResult eff(Eff eff, LivingEntity entity);

    /**
     * 行动中
     *
     * @param scenario 场景
     * @param cdl      控制进程
     * @param as       对方
     * @return
     */
    public abstract Object letDo(Scenario scenario, CountDownLatch cdl, LivingEntity[] as);

    /**
     * 准备行动 默认无
     *
     * @param scenario
     * @param cdl
     * @param as
     * @return
     */
    public Object letDoPre(Scenario scenario, CountDownLatch cdl, LivingEntity[] as) {
        return "";
    }

    /**
     * 加或减hp
     *
     * @return
     */
    public LivingEntity asHp(Integer v) {
        this.hp += v;
        int max = getMaxHp().getFinalValue();
        if (this.hp > max) this.hp = max;
        else if (this.hp < 0) {
            this.hp = 0;
            state = -1;
        }
        return this;
    }
}
