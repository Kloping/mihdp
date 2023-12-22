package io.github.kloping.mihdp.game.impl;

import io.github.kloping.mihdp.game.api.User;
import io.github.kloping.mihdp.game.dao.Item;

/**
 * @author github.kloping
 */
public class ItemImpl extends Item {
    @Override
    public boolean use(User active, User passive) {
        return false;
    }
}
