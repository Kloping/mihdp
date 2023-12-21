package io.github.kloping.mihdp.game.impl;

import com.alibaba.fastjson.JSONObject;
import io.github.kloping.mihdp.game.api.logic.LogicBase;
import io.github.kloping.mihdp.game.dao.CharacterDetail;

/**
 * @author github.kloping
 */
public class CharacterLogic implements LogicBase<Character, JSONObject, CharacterDetail> {
    public CharacterLogic() {

    }

    @Override
    public CharacterDetail logic(Character character, JSONObject jsonObject) {
        return null;
    }
}
