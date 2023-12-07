package io.github.kloping.mihdp.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author github.kloping
 */
@Data
@Accessors(chain = true)
public class UsersResources {
    /**
     * 对应user uid
     */
    @TableId
    private String uid;
    /**
     * 积分
     */
    private Integer score = 1000;
    /**
     * 存的积分
     */
    private Integer score0 = 200;
    /**
     * 特殊币
     */
    private Integer gold = 0;
    /**
     * 体力
     */
    public Integer energy = 200;
}
