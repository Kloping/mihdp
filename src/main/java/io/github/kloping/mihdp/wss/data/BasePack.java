package io.github.kloping.mihdp.wss.data;

import com.alibaba.fastjson.JSON;
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
public class BasePack {
    private String name;
    private String msg;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
