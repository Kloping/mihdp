package io.github.kloping.mihdp.game.v;

import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.spt.RedisOperate;

/**
 * @author github.kloping
 */
@Entity
public class RedisSource {
    @AutoStand
    public RedisOperate<Integer> cid2hp;
    @AutoStand
    public RedisOperate<Integer> cid2xp;

}
