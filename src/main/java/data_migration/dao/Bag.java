package data_migration.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author github.kloping
 */
@Data
public class Bag {
    @TableId
    private Integer id;
    private Integer oid;
    private Long qid;
    private Long time;
    private String desc;
    private Integer state;
}
