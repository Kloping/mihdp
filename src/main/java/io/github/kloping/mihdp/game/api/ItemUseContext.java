package io.github.kloping.mihdp.game.api;

import io.github.kloping.arr.Class2OMap;
import io.github.kloping.mihdp.game.v.v1.ItemAboController;

/**
 * @author github.kloping
 */
public interface ItemUseContext {
    /**
     *
     * @param map
     * @return
     */
    ItemAboController.UseState execute(Class2OMap map);
}
