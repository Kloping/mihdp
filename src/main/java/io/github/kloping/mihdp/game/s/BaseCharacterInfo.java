package io.github.kloping.mihdp.game.s;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author github.kloping
 */
@Data
@Accessors(chain = true)
public class BaseCharacterInfo {
    protected Integer xp;
    protected Integer hp;
    protected Integer hl;
    protected Integer hj;
    protected Integer att;
    /**
     * 防御
     */
    protected Integer defense;
    /**
     * 速度
     */
    protected Integer speed;
    /**
     * 爆率
     */
    protected Integer chc;
    /**
     * 爆伤
     */
    protected Integer che;
    /**
     * 效果命中
     */
    protected Integer efr;
    /**
     * 效果抵抗
     */
    protected Integer efh;
}
