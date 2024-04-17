package io.github.kloping.mihdp.game.dao;

import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Type;

/**
 *
 * @author github.kloping
 */
@Data
@Accessors(chain = true)
public class BaseCharacterInfo {

    public static final Type TYPE_TOKEN = new TypeToken<BaseCharacterInfo>() {
    }.getType();

    protected Attr xp;
    protected Attr hp;
    protected Attr hl;
    protected Attr hj;
    protected Attr att;
    /**
     * 防御
     */
    protected Attr defense;
    /**
     * 速度
     */
    protected Attr speed;
    /**
     * 爆率
     */
    protected Attr chc;
    /**
     * 爆伤
     */
    protected Attr che;
    /**
     * 效果命中
     */
    protected Attr efr;
    /**
     * 效果抵抗
     */
    protected Attr efh;
}
