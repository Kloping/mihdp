package io.github.kloping.mihdp.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 魂角
 * @author github.kloping
 */
@Data
@Accessors(chain = true)
public class Characters {
    /**
     * 自增id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 拥有者id
     */
    private String uid;
    /**
     * 角色id
     */
    private Integer cid;
    /**
     * 角色等级
     */
    private Integer level;
    /**
     * 主武器id
     */
    private Integer mid;

    private Integer hp;
}
