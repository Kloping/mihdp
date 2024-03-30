package io.github.kloping.mihdp.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.kloping.mihdp.mapper.BagMaper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author github.kloping
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Bag {
    private String uid;
    /**
     * 资源id
     */
    private Integer rid;
    /**
     * 资源数量
     */
    private Integer num;
    /**
     * 占用大小
     */
    private Integer size;

    public void save(BagMaper bagMaper) {
        QueryWrapper<Bag> qw = new QueryWrapper<>();
        qw.eq("uid", uid);
        qw.eq("rid", rid);
        if (num <= 0) {
            bagMaper.delete(qw);
        } else {
            bagMaper.update(this, qw);
        }
    }
}
