package io.github.kloping.mihdp.game.s;

import com.alibaba.fastjson.annotation.JSONField;
import io.github.kloping.judge.Judge;
import io.github.kloping.mihdp.game.api.Addition;
import io.github.kloping.mihdp.game.api.Additive;
import io.github.kloping.number.NumberUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * @author github.kloping
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class CharactersInfo extends BaseCharacterInfo implements Additive {
    private Integer id;
    private String name;
    private Integer star;
    private Integer level;
    /**
     * 突破加成属性
     */
    private String bt;
    /**
     * 突破加成值
     */
    private Integer btv;

    @JSONField(serialize = false, deserialize = false)
    private List<Addition> additionList = new LinkedList<>();

    @Override
    public void reg(Addition addition) {
        if (addition == null) return;
        String name = addition.getAttr();
        if (Judge.isEmpty(name)) return;
        try {
            Field field = BaseCharacterInfo.class.getDeclaredField(name);
            field.setAccessible(true);
            Integer value = (Integer) field.get(this);
            if (addition.getType() == 1) {
                value += addition.getValue();
                field.set(this, value);
            } else if (addition.getType() == 2) {
                Integer v0 = Math.toIntExact(NumberUtils.percentTo(value, addition.getValue()));
                value += v0;
                field.set(this, value);
            } else return;
            additionList.add(addition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
