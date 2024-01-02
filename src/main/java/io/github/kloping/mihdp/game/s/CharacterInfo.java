package io.github.kloping.mihdp.game.s;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.reflect.TypeToken;
import io.github.kloping.judge.Judge;
import io.github.kloping.mihdp.game.api.Addition;
import io.github.kloping.mihdp.game.api.Additive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * @author github.kloping
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class CharacterInfo extends BaseCharacterInfo implements Additive {

    public static final Type TYPE_TOKEN = new TypeToken<CharacterInfo>() {
    }.getType();

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
            Attr value = (Attr) field.get(this);
            if (addition.getType() == 1) {
                value.setBaseValue(value.getBaseValue() + addition.getValue());
            } else if (addition.getType() == 2) {
                value.setBv(value.getBv() + addition.getValue());
            } else return;
            additionList.add(addition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
