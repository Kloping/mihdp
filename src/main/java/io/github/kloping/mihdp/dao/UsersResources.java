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
    /**
     * 签到标记
     */
    private Integer day = 0;
    /**
     * 签到天数
     */
    private Integer days = 0;
    /**
     * 犯罪指数
     */
    private Integer fz = 0;
    /**
     * 打工cd
     */
    private Long k = 0L;

    public void addFz(int i) {
        fz += i;
    }
}
