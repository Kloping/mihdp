package io.github.kloping.mihdp.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author github.kloping
 */
@Data
public class Config {
    @TableId
    private String key;
    private String value;
}
