package io.github.kloping.mihdp.dao;

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
    private String uid;
    /**
     * 积分
     */
    private Integer score;
    /**
     * 存的积分
     */
    private Integer score0;
    /**
     * 特殊币
     */
    private Integer gold;
}
