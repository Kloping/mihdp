package io.github.kloping.mihdp.game.entity.csn;

import io.github.kloping.mihdp.game.dao.CharacterInfo;
import io.github.kloping.mihdp.game.entity.Damage;
import io.github.kloping.mihdp.game.entity.DamageResult;
import io.github.kloping.mihdp.game.entity.LivingEntity;

/**
 * 魂角作战 基础
 *
 * @author github.kloping
 */
public class CiBase extends LivingEntity {
    private CharacterInfo characterInfo;

    @Override
    public DamageResult ack(Damage damage, LivingEntity entity) {
        return null;
    }

    @Override
    public boolean attach(Integer type, LivingEntity entity) {
        return false;
    }
}
