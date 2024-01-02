package data_migration.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author github.kloping
 */
@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class Cycle {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 对应 {@link Character#getId()}
     */
    private Integer cid;
    /**
     * 物id
     */
    private Integer oid;
}
