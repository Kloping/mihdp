package io.github.kloping.mihdp.dao;

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
}
