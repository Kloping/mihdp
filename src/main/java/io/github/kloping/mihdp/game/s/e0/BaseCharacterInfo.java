package io.github.kloping.mihdp.game.s.e0;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author github.kloping
 */
@Data
@Accessors(chain = true)
public class BaseCharacterInfo {
    private Integer xp;
    private Integer hp;
    private Integer hj;
    private Integer att;
    /**
     * 防御
     */
    private Integer defense;
    /**
     * 速度
     */
    private Integer speed;
    /**
     * 爆率
     */
    private Integer chc;
    /**
     * 爆伤
     */
    private Integer che;
    /**
     * 效果命中
     */
    private Integer efr;
    /**
     * 效果抵抗
     */
    private Integer efh;
}
