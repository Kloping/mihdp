package io.github.kloping.mihdp.game.entity.csn;

import io.github.kloping.mihdp.game.dao.CharacterInfo;
import io.github.kloping.mihdp.game.entity.Eff;
import io.github.kloping.mihdp.game.entity.EffResult;
import io.github.kloping.mihdp.game.entity.LivingEntity;

/**
 * 魂角作战 基础
 *
 * @author github.kloping
 */
public class CiBase extends LivingEntity {
    private CharacterInfo characterInfo;

    @Override
    public EffResult ack(Eff damage, LivingEntity entity) {
        if (entity == null || entity.getHp() <= 0) {
            return new EffResult(damage.getType(), damage.getValue(), damage.getValue(), 1);
        } else if (damage.getValue() > 0) {

        }
        return null;
    }

    @Override
    public boolean attach(Integer type, LivingEntity entity) {
        return false;
    }
}