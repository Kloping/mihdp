package io.github.kloping.mihdp.game.service.gos;

import io.github.kloping.mihdp.game.dao.Attr;
import io.github.kloping.mihdp.game.scenario.Scenario;
import io.github.kloping.mihdp.game.service.EffResult;
import io.github.kloping.mihdp.game.service.LivingEntity;
import io.github.kloping.mihdp.game.service.csn.GoBase;
import io.github.kloping.mihdp.game.service.effs.AttEff;
import io.github.kloping.number.NumberUtils;
import io.github.kloping.rand.RandomUtils;

import java.util.concurrent.CountDownLatch;

/**
 * @author github.kloping
 */
public class GoBaseImpl3002 extends GoBase {
    public static final int ID = 3002;

    /**
     * @param level 1-150
     */
    public GoBaseImpl3002(Integer level) {
        super(getId(), ID);

        maxHp = new Attr("hp", level * 100);
        att = new Attr("att", level + 12);
        defense = new Attr("defense", level);

        hp = maxHp.getFinalValue();
        speed = new Attr("speed", 96 + RandomUtils.RANDOM.nextInt(15));
        chc = new Attr("chc", 0);
        che = new Attr("che", 50);
        efh = new Attr("efh", 50);
        efr = new Attr("efr", 50);
        distance = 1000;
    }

    @Override
    public Object letDo(Scenario scenario, CountDownLatch cdl, LivingEntity[] as) {
        LivingEntity entity = RandomUtils.getRand(as);
        Integer avl = NumberUtils.percentTo(this.getAtt().getFinalValue(), 50).intValue();
        EffResult result = eff(new AttEff(avl), entity);
        return String.format("红足虫使用了范围攻击对全体造成%s点伤害(%s)", result.getValue(), result.getStateTips());
    }

    @Override
    public int[] getFallObjs() {
        int r = RandomUtils.RANDOM.nextInt(10);
        if (r < 7) return new int[]{2002, 101, 101};
        else if (r < 9) return new int[]{2002, 2001, 101, 101};
        else return new int[]{101, 101};
    }
}
