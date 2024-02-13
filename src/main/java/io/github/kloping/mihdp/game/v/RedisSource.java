package io.github.kloping.mihdp.game.v;

import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.spt.RedisOperate;

/**
 * @author github.kloping
 */
@Entity
public class RedisSource {
    /**
     * 常规 不删
     */
    @AutoStand(id = "0")
    public RedisOperate<Integer> str2int;
    /**
     * 每周末删
     */
    @AutoStand(id = "1")
    public RedisOperate<Integer> id2shopMax;
    /**
     * 永久不删
     */
    @AutoStand(id = "2")
    public RedisOperate<String> id2ent;
}
