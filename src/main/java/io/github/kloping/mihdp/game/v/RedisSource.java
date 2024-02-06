package io.github.kloping.mihdp.game.v;

import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.spt.RedisOperate;

/**
 * @author github.kloping
 */
@Entity
public class RedisSource {
    @AutoStand(id = "0")
    public RedisOperate<Integer> str2int;
    @AutoStand(id = "1")
    public RedisOperate<Integer> id2shopMax;
}
