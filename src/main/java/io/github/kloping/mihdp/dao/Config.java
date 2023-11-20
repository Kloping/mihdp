package io.github.kloping.mihdp.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import io.github.kloping.object.ObjectUtils;
import lombok.Data;

/**
 * @author github.kloping
 */
@Data
public class Config<T> {
    @TableId
    private String key;
    private String value;

    public T as(Class<T> c) {
        return ObjectUtils.asPossible(c, value);
    }
}
