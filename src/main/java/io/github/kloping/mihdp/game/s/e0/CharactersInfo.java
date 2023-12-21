package io.github.kloping.mihdp.game.s.e0;

import lombok.Data;

/**
 * @author github.kloping
 */
@Data
public class CharactersInfo {
    private Integer id;
    private String name;
    private Integer star;
    /**
     * 突破加成属性
     */
    private String bt;
    /**
     * 突破加成值
     */
    private Integer btv;
}
