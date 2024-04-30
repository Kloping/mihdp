package io.github.kloping.mihdp.game.service.csn;

import io.github.kloping.mihdp.game.dao.CharacterInfo;
import io.github.kloping.mihdp.game.service.Eff;
import io.github.kloping.mihdp.game.service.EffResult;
import io.github.kloping.mihdp.game.service.LivingEntity;

/**
 * 魂角作战 基础
 *
 * @author github.kloping
 */
public class CiBase extends LivingEntity {

    @Override
    public EffResult eff(Eff eff, LivingEntity entity) {
        if (entity == null || entity.getHp() <= 0) {
            return new EffResult(eff.getType(), eff.getValue(), eff.getValue(), 1);
        } else if (eff.getValue() > 0) {
            return eff.fun(this, entity);
        }
        return null;
    }

    @Override
    public boolean attach(Integer type, LivingEntity entity) {
        return false;
    }
}