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
     * 常规 不删 魂角信息
     */
    @AutoStand(id = "0")
    public RedisOperate<Integer> str2int;
    /**
     * 每周末删 商城记录
     */
    @AutoStand(id = "1")
    public RedisOperate<Integer> weekDel;
    /**
     * 永久不删 目前灵力记录
     */
    @AutoStand(id = "2")
    public RedisOperate<String> id2ent;
    /**
     * 当前用户使用的魂角
     */
    @AutoStand(id = "3")
    public RedisOperate<Integer> uid2cid;
    /**
     * 用户修炼cd
     */
    @AutoStand(id = "4")
    public RedisOperate<Long> uid2cd;
    /**
     * 用户副本cd
     */
    @AutoStand(id = "5")
    public RedisOperate<Long> uid2fb;
}
