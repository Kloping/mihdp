package io.github.kloping.mihdp.game.service.gos;

import io.github.kloping.mihdp.game.dao.Attr;
import io.github.kloping.mihdp.game.scenario.Scenario;
import io.github.kloping.mihdp.game.service.LivingEntity;
import io.github.kloping.mihdp.game.service.csn.GoBase;
import io.github.kloping.rand.RandomUtils;

import java.util.concurrent.CountDownLatch;

/**
 * @author github.kloping
 */
public class GoBaseImpl3001 extends GoBase {
    public static final int ID = 3001;

    /**
     * @param level 1-150
     */
    public GoBaseImpl3001(Integer level) {
        super(getId(), ID);

        maxHp = new Attr("hp", level * 100).setBaseValue(level * 100);
        att = new Attr("att", level + 10).setBaseValue(10);
        defense = new Attr("defense", level).setBaseValue(10);

        hp = maxHp.getFinalValue();
        speed = new Attr("speed", 110);
        chc = new Attr("chc", 0);
        che = new Attr("che", 50);
        efh = new Attr("efh", 50);
        efr = new Attr("efr", 50);
        distance = 1000;
    }

    @Override
    public Object letDo(Scenario scenario, CountDownLatch cdl, LivingEntity[] as) {
        return super.letDo(scenario, cdl, as);
    }

    @Override
    public int[] getFallObjs() {
        int r = RandomUtils.RANDOM.nextInt(10);
        if (r < 8) return new int[]{2001, 101, 101};
        else if (r < 9) return new int[]{2001, 2001, 101, 101};
        else return new int[]{101, 101};
    }
}
