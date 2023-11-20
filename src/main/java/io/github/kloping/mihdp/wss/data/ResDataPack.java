package io.github.kloping.mihdp.wss.data;

import com.alibaba.fastjson.JSON;
import io.github.kloping.mihdp.ex.GeneralData;
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
public class ResDataPack {
    private String action;

    private String id;
    private GeneralData data;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
