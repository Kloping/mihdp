package io.github.kloping.mihdp.game.dao;

import io.github.kloping.mihdp.game.api.Usable;
import lombok.Data;

/**
 * @author github.kloping
 */
@Data
public abstract class Item implements Usable {
    private Integer id;
    private String name;
    private String src;
    private String desc;
    private Integer price;
}
