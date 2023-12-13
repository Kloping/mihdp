package data_migration.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author github-kloping
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserScore implements Serializable {
    @TableId
    private String id;
    private Integer score = 1000;
    private Integer score0 = 200;
    private Integer xp = 0;
    private Integer xpl = 100;
    private Integer level = 1;

    private Integer day = 0;
    private Integer days = 0;
    private Integer fz = 0;
    private Long k = 0L;

    public void addXp(int i) {
        xp += i;
        if (xp >= xpl) {
            xp = 0;
            xpl += 50;
            level++;
        }
    }
}