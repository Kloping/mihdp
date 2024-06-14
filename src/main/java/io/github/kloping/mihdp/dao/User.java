package io.github.kloping.mihdp.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * @author github.kloping
 */
@Data
@Accessors(chain = true)
public class User {
    /**
     * 唯一id
     */
    @TableId
    private String id;
    /**
     * 游戏内唯一标识
     */
    private String uid;
    /**
     * 不唯一名字
     */
    private String name = "默认昵称";
    /**
     * 暂时可空
     */
    private String icon = "";
    /**
     * 经验值
     */
    private Integer xp = 0;
    /**
     * 等级
     */
    private Integer level = 1;
    /**
     * 注册时间
     */
    private Long reg = System.currentTimeMillis();

    public void addXp(int i) {
        this.xp += i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(uid, user.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uid, name, icon, xp, level, reg);
    }
}
