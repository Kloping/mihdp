package io.github.kloping.mihdp.game.service.effs;

import io.github.kloping.mihdp.game.service.Eff;
import io.github.kloping.mihdp.game.service.EffResult;
import io.github.kloping.mihdp.game.service.LivingEntity;

/**
 * @author github.kloping
 */
public class AttEff extends Eff {

    public AttEff(Integer value) {
        super(1, value);
    }

    @Override
    public EffResult fun(LivingEntity entity0, LivingEntity entity1) {

        return null;
    }
}
