package io.github.kloping.mihdp.game.scenario;

import io.github.kloping.judge.Judge;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.service.LivingEntity;
import io.github.kloping.mihdp.utils.ImageDrawer;
import io.github.kloping.number.NumberUtils;

import java.util.concurrent.CountDownLatch;

/**
 * @author github.kloping
 */
public class ScenarioImpl implements Scenario {
    /**
     * 场景a方
     */
    LivingEntity[] as;
    /**
     * 场景b方
     */
    LivingEntity[] bs;
    /**
     * 默认场景距离1w m
     */
    public static final Integer MAX_JOURNEY = 10000;

    private Boolean run = true;
    private ScenarioManager manager;

    public ScenarioImpl(LivingEntity[] as, LivingEntity[] bs, ScenarioManager manager) {
        this.as = as;
        this.bs = bs;
        this.manager = manager;
    }

    @Override
    public void run() {
        //循环 直至下一个实体行动
        while (run) {
            try {
                test0();
                allV1();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void test0() {
        boolean k = false;
        for (LivingEntity e : as) {
            if (e.getHp() != 0) k = true;
        }
        if (!k) destroy(as);
        k = false;
        for (LivingEntity e : bs) {
            if (e.getHp() != 0) k = true;
        }
        if (!k) destroy(bs);
    }

    private void step(LivingEntity e, Integer bv, LivingEntity[] as) {
        int d0 = NumberUtils.percentTo(bv, e.getSpeed().getFinalValue()).intValue();
        e.distance = e.distance - d0;
        if (e.distance == 0) {

            Object o = e.letDoPre(this, cdl, as);
            if (o instanceof String) {
                if (Judge.isNotEmpty(o.toString())) {
                    builder.append("\n").append(o.toString());
                }
            } else if (o instanceof GeneralData) {
                builder.append((GeneralData) o);
            }


            o = e.letDo(this, cdl, as);
            if (o instanceof String) {
                if (Judge.isNotEmpty(o.toString())) {
                    builder.append("\n").append(o.toString());
                }
            } else if (o instanceof GeneralData) {
                builder.append((GeneralData) o);
            }

            e.distance = MAX_JOURNEY;
        }
    }

    private LivingEntity hasNv() {
        for (LivingEntity e : as) {
            try {
                if (e.getDistance() < e.getSpeed().getFinalValue()) {
                    return e;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        for (LivingEntity e : bs) {
            try {
                if (e.getDistance() < e.getSpeed().getFinalValue()) {
                    return e;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public void allV1() {
        LivingEntity e0 = hasNv();
        int bv = 100;
        if (e0 != null) bv = NumberUtils.toPercent(e0.getDistance(), e0.getSpeed().getFinalValue());
        for (LivingEntity e : as) {
            step(e, bv, bs);
            p = as;
        }
        for (LivingEntity e : bs) {
            step(e, bv, as);
            p = bs;
        }
    }

    @Override
    public void destroy(LivingEntity[] as) {
        run = false;
        manager.remove(this);
    }

    private LivingEntity[] p = null;

    private GeneralData.GeneralDataBuilder builder = new GeneralData.GeneralDataBuilder();

    public CountDownLatch cdl = new CountDownLatch(1);

    @Override
    public Object out() {
        try {
            cdl.await();
            cdl = new CountDownLatch(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return builder.build().getList().add(drawScenario());
    }

    @Override
    public Object getTips() {
        return builder.build();
    }

    @Override
    public LivingEntity[] getCurrentEntities() {
        return p;
    }

    private GeneralData drawScenario() {
        return null;
    }
}
