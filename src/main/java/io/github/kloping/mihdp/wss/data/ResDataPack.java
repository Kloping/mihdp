package io.github.kloping.mihdp.wss.data;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

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
    private ResData data;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
